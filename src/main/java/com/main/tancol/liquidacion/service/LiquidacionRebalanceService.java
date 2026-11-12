package com.main.tancol.liquidacion.service;

import com.main.tancol.liquidacion.entities.LiquidacionEntity;
import com.main.tancol.liquidacion.entities.LiquidacionPartidaEntity;
import com.main.tancol.liquidacion.entities.LiquidacionTotalizadoEmpleadoEntity;
import com.main.tancol.liquidacion.entities.LiquidacionTotalizadoEntity;
import com.main.tancol.liquidacion.enums.EstadosLiquidacion;
import com.main.tancol.producto.entities.comerciales.ProductoComercialEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
public class LiquidacionRebalanceService {

    public void rebalancePorCancelacionPartida(LiquidacionEntity liquidacion, LiquidacionPartidaEntity partida) {
        restarPartidaDeTotalizado(liquidacion, partida, true);
        recalcularTotalLiquidado(liquidacion);
    }

    public void rebalancePorReemplazoPartida(LiquidacionEntity liquidacion,
                                             LiquidacionPartidaEntity cancelada,
                                             LiquidacionPartidaEntity nueva) {
        UUID productoAnterior = idProducto(cancelada);
        UUID productoNuevo = idProducto(nueva);
        BigDecimal cantidadAnterior = cantidadSegura(cancelada.getCantidad());
        BigDecimal cantidadNueva = cantidadSegura(nueva.getCantidad());

        if (productoAnterior != null && productoAnterior.equals(productoNuevo)) {
            BigDecimal delta = cantidadNueva.subtract(cantidadAnterior);
            LiquidacionTotalizadoEntity totalizado = findTotalizado(liquidacion, productoAnterior);
            if (totalizado == null && cantidadNueva.compareTo(BigDecimal.ZERO) > 0) {
                totalizado = crearTotalizadoDesdePlantilla(liquidacion, nueva.getProductoComercial(), null);
            }
            if (totalizado != null) {
                aplicarDelta(totalizado, delta);
                if (totalizadoVacio(totalizado) && cantidadNueva.compareTo(BigDecimal.ZERO) <= 0) {
                    liquidacion.removerTotalizado(totalizado);
                }
            }
            recalcularTotalLiquidado(liquidacion);
            return;
        }

        LiquidacionTotalizadoEntity plantilla = productoAnterior == null ? null : findTotalizado(liquidacion, productoAnterior);
        restarPartidaDeTotalizado(liquidacion, cancelada, true);
        if (productoNuevo != null && cantidadNueva.compareTo(BigDecimal.ZERO) > 0) {
            LiquidacionTotalizadoEntity destino = findTotalizado(liquidacion, productoNuevo);
            if (destino == null) {
                destino = crearTotalizadoDesdePlantilla(liquidacion, nueva.getProductoComercial(), plantilla);
            }
            sumarCantidadAChoferes(destino, cantidadNueva);
        }
        recalcularTotalLiquidado(liquidacion);
    }

    public void limpiarTotalizados(LiquidacionEntity liquidacion) {
        liquidacion.clearTotalizados();
        liquidacion.setTotalLiquidado(BigDecimal.ZERO);
    }

    public void recalcularTotalLiquidado(LiquidacionEntity liquidacion) {
        if (!EstadosLiquidacion.FINALIZADA.equals(liquidacion.getEstadosLiquidacion())) {
            return;
        }

        BigDecimal total = liquidacion.getLiquidacionPartida().stream()
                .filter(p -> !p.isCancelada())
                .map(LiquidacionPartidaEntity::getImporte)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        liquidacion.setTotalLiquidado(total);
    }

    private void restarPartidaDeTotalizado(LiquidacionEntity liquidacion, LiquidacionPartidaEntity partida, boolean removerSiVacio) {
        if (partida.getProductoComercial() == null
                || partida.getCantidad() == null
                || partida.getCantidad().compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        UUID productoId = partida.getProductoComercial().getId();
        LiquidacionTotalizadoEntity totalizado = findTotalizado(liquidacion, productoId);
        if (totalizado == null) {
            return;
        }
        restarCantidadDeChoferes(totalizado, partida.getCantidad());
        if (removerSiVacio && totalizadoVacio(totalizado)) {
            liquidacion.removerTotalizado(totalizado);
        }
    }

    private void aplicarDelta(LiquidacionTotalizadoEntity totalizado, BigDecimal delta) {
        if (delta.compareTo(BigDecimal.ZERO) > 0) {
            sumarCantidadAChoferes(totalizado, delta);
        } else if (delta.compareTo(BigDecimal.ZERO) < 0) {
            restarCantidadDeChoferes(totalizado, delta.abs());
        }
    }

    private void restarCantidadDeChoferes(LiquidacionTotalizadoEntity totalizado, BigDecimal cantidadRestar) {
        List<LiquidacionTotalizadoEmpleadoEntity> choferes = empleadosReparto(totalizado);
        if (choferes.isEmpty()) {
            return;
        }

        BigDecimal totalChoferes = choferes.stream()
                .map(this::cantidadSegura)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalChoferes.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        BigDecimal pendiente = cantidadRestar.min(totalChoferes);
        for (int i = 0; i < choferes.size(); i++) {
            LiquidacionTotalizadoEmpleadoEntity chofer = choferes.get(i);
            BigDecimal cantidadChofer = cantidadSegura(chofer);
            if (cantidadChofer.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            BigDecimal resta;
            if (i == choferes.size() - 1) {
                resta = pendiente;
            } else {
                resta = cantidadRestar
                        .multiply(cantidadChofer)
                        .divide(totalChoferes, 2, RoundingMode.HALF_UP)
                        .min(pendiente);
            }

            chofer.setCantidad(cantidadChofer.subtract(resta).max(BigDecimal.ZERO));
            pendiente = pendiente.subtract(resta);
            if (pendiente.compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }
        }
    }

    private void sumarCantidadAChoferes(LiquidacionTotalizadoEntity totalizado, BigDecimal cantidadSumar) {
        if (cantidadSumar == null || cantidadSumar.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        List<LiquidacionTotalizadoEmpleadoEntity> choferes = empleadosReparto(totalizado);
        if (choferes.isEmpty()) {
            log.warn("No hay choferes para reponer {} en el totalizado {}", cantidadSumar, totalizado.getId());
            return;
        }

        BigDecimal totalChoferes = choferes.stream()
                .map(this::cantidadSegura)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal pendiente = cantidadSumar;
        for (int i = 0; i < choferes.size(); i++) {
            LiquidacionTotalizadoEmpleadoEntity chofer = choferes.get(i);
            BigDecimal suma;
            if (i == choferes.size() - 1) {
                suma = pendiente;
            } else if (totalChoferes.compareTo(BigDecimal.ZERO) <= 0) {
                suma = cantidadSumar.divide(BigDecimal.valueOf(choferes.size()), 2, RoundingMode.HALF_UP);
            } else {
                suma = cantidadSumar
                        .multiply(cantidadSegura(chofer))
                        .divide(totalChoferes, 2, RoundingMode.HALF_UP);
            }
            chofer.setCantidad(cantidadSegura(chofer).add(suma));
            pendiente = pendiente.subtract(suma);
        }
    }

    private LiquidacionTotalizadoEntity crearTotalizadoDesdePlantilla(LiquidacionEntity liquidacion,
                                                                      ProductoComercialEntity producto,
                                                                      LiquidacionTotalizadoEntity plantilla) {
        LiquidacionTotalizadoEntity origen = plantilla != null ? plantilla : primeraPlantilla(liquidacion);
        LiquidacionTotalizadoEntity nuevo = clonarEstructuraChoferes(origen, producto);
        liquidacion.agregarTotalizado(nuevo);
        return nuevo;
    }

    private LiquidacionTotalizadoEntity primeraPlantilla(LiquidacionEntity liquidacion) {
        if (liquidacion.getTotalesPorProductos() == null) {
            return null;
        }
        return liquidacion.getTotalesPorProductos().stream().findFirst().orElse(null);
    }

    private LiquidacionTotalizadoEntity clonarEstructuraChoferes(LiquidacionTotalizadoEntity origen,
                                                                 ProductoComercialEntity producto) {
        LiquidacionTotalizadoEntity nuevo = LiquidacionTotalizadoEntity.builder()
                .productoComercial(producto)
                .empleadoTotalizadoLiquidacion(new LinkedList<>())
                .build();
        if (origen == null || origen.getEmpleadoTotalizadoLiquidacion() == null) {
            return nuevo;
        }

        List<LiquidacionTotalizadoEmpleadoEntity> choferes = origen.getEmpleadoTotalizadoLiquidacion().stream()
                .filter(e -> e.getChoferOrigen() == null)
                .toList();
        for (LiquidacionTotalizadoEmpleadoEntity chofer : choferes) {
            LiquidacionTotalizadoEmpleadoEntity copiaChofer = copiarEmpleado(chofer, nuevo, true);
            if (chofer.getAyudantes() != null) {
                for (LiquidacionTotalizadoEmpleadoEntity ayudante : chofer.getAyudantes()) {
                    LiquidacionTotalizadoEmpleadoEntity copiaAyudante = copiarEmpleado(ayudante, nuevo, false);
                    copiaChofer.addAyudante(copiaAyudante);
                }
            }
        }
        return nuevo;
    }

    private LiquidacionTotalizadoEmpleadoEntity copiarEmpleado(LiquidacionTotalizadoEmpleadoEntity origen,
                                                               LiquidacionTotalizadoEntity totalizado,
                                                               boolean esChofer) {
        LiquidacionTotalizadoEmpleadoEntity copia = LiquidacionTotalizadoEmpleadoEntity.builder()
                .empleado(origen.getEmpleado())
                .cantidad(BigDecimal.ZERO)
                .chofer(esChofer)
                .ayudantes(new LinkedList<>())
                .build();
        copia.setOrigenTotalizado(totalizado);
        return copia;
    }

    private LiquidacionTotalizadoEntity findTotalizado(LiquidacionEntity liquidacion, UUID productoId) {
        if (productoId == null || liquidacion.getTotalesPorProductos() == null) {
            return null;
        }
        return liquidacion.getTotalesPorProductos().stream()
                .filter(t -> t.getProductoComercial() != null && productoId.equals(t.getProductoComercial().getId()))
                .findFirst()
                .orElse(null);
    }

    private List<LiquidacionTotalizadoEmpleadoEntity> empleadosReparto(LiquidacionTotalizadoEntity totalizado) {
        List<LiquidacionTotalizadoEmpleadoEntity> empleados = totalizado.getEmpleadoTotalizadoLiquidacion();
        if (empleados == null || empleados.isEmpty()) {
            return List.of();
        }
        List<LiquidacionTotalizadoEmpleadoEntity> choferes = empleados.stream()
                .filter(e -> Boolean.TRUE.equals(e.getChofer()))
                .toList();
        return choferes.isEmpty() ? new ArrayList<>(empleados) : new ArrayList<>(choferes);
    }

    private boolean totalizadoVacio(LiquidacionTotalizadoEntity totalizado) {
        List<LiquidacionTotalizadoEmpleadoEntity> empleados = totalizado.getEmpleadoTotalizadoLiquidacion();
        if (empleados == null || empleados.isEmpty()) {
            return true;
        }
        return empleados.stream()
                .allMatch(e -> cantidadSegura(e).compareTo(BigDecimal.ZERO) <= 0
                        && (e.getAyudantes() == null || e.getAyudantes().stream()
                        .allMatch(a -> cantidadSegura(a).compareTo(BigDecimal.ZERO) <= 0)));
    }

    private UUID idProducto(LiquidacionPartidaEntity partida) {
        return partida.getProductoComercial() == null ? null : partida.getProductoComercial().getId();
    }

    private BigDecimal cantidadSegura(LiquidacionTotalizadoEmpleadoEntity empleado) {
        return empleado.getCantidad() != null ? empleado.getCantidad() : BigDecimal.ZERO;
    }

    private BigDecimal cantidadSegura(BigDecimal cantidad) {
        return cantidad != null ? cantidad : BigDecimal.ZERO;
    }
}
