package com.main.tancol.liquidacion.service;

import com.main.tancol.common.exceptions.exeption.GenericExeption;
import com.main.tancol.empleados.entities.EmpleadosEntity;
import com.main.tancol.liquidacion.entities.LiquidacionEntity;
import com.main.tancol.liquidacion.entities.LiquidacionTotalizadoEmpleadoEntity;
import com.main.tancol.liquidacion.entities.LiquidacionTotalizadoEntity;
import com.main.tancol.liquidacion.enums.EstadosLiquidacion;
import com.main.tancol.logistica.entities.bitacora.TrayectoEntity;
import com.main.tancol.logistica.entities.bitacora.VentaTrayectoEntity;
import com.main.tancol.logistica.entities.bitacora.comisiones.VentasEmpleadoEntity;
import com.main.tancol.producto.entities.comerciales.ProductoComercialEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
public class LiquidacionConteoTrayectoService {

    public static final String MSG_RUTA =
            "No se puede cambiar la ruta de un trayecto ya enviado a liquidación";
    public static final String MSG_CERRADA =
            "No se puede corregir el conteo: la liquidación de esta ruta ya está finalizada o cancelada";
    public static final String MSG_AMBIGUO =
            "No se puede corregir el conteo: hay más de un totalizado con el mismo producto, chofer y cantidad, y no se puede saber cuál corregir";
    public static final String MSG_NO_ENCONTRADO =
            "No se puede corregir el conteo: no se encontró en la liquidación la cantidad enviada por este trayecto";

    public Map<UUID, BigDecimal> cantidadesVendidas(TrayectoEntity viaje) {
        Map<UUID, BigDecimal> cantidades = new LinkedHashMap<>();
        for (Acumulado acumulado : agrupar(viaje).values()) {
            cantidades.put(acumulado.producto.getId(), acumulado.cantidad);
        }
        return cantidades;
    }

    public boolean cambioElConteo(Map<UUID, BigDecimal> antes, Map<UUID, BigDecimal> despues) {
        Map<UUID, BigDecimal> anterior = antes == null ? Map.of() : antes;
        Map<UUID, BigDecimal> actual = despues == null ? Map.of() : despues;
        if (anterior.size() != actual.size()) {
            return true;
        }
        for (Map.Entry<UUID, BigDecimal> entrada : anterior.entrySet()) {
            if (!actual.containsKey(entrada.getKey()) || !mismaCantidad(entrada.getValue(), actual.get(entrada.getKey()))) {
                return true;
            }
        }
        return false;
    }

    public void rechazarCambioDeRuta(boolean rutaCambio, boolean yaEnviado) {
        if (rutaCambio && yaEnviado) {
            throw new GenericExeption(MSG_RUTA);
        }
    }

    public Rastro rastrear(List<LiquidacionEntity> liquidaciones,
                           TrayectoEntity viaje,
                           Map<UUID, BigDecimal> anteriores,
                           UUID choferOriginalId) {
        List<LiquidacionEntity> fuente = liquidaciones == null ? List.of() : liquidaciones;
        List<LiquidacionEntity> conVinculo = new ArrayList<>();
        for (LiquidacionEntity liquidacion : fuente) {
            if (tieneVinculo(liquidacion, viaje)) {
                conVinculo.add(liquidacion);
            }
        }
        if (conVinculo.size() > 1) {
            return Rastro.ambiguo();
        }
        if (conVinculo.size() == 1) {
            return Rastro.vinculado(conVinculo.get(0));
        }

        Map<UUID, BigDecimal> conteoAnterior = anteriores == null ? Map.of() : anteriores;
        if (conteoAnterior.isEmpty() || choferOriginalId == null) {
            return Rastro.ninguno();
        }

        List<Coincidencia> coincidencias = new ArrayList<>();
        boolean algunProductoSinFila = false;
        boolean algunProductoRepetido = false;
        for (Map.Entry<UUID, BigDecimal> entrada : conteoAnterior.entrySet()) {
            List<Coincidencia> candidatos = new ArrayList<>();
            for (LiquidacionEntity liquidacion : fuente) {
                for (LiquidacionTotalizadoEntity totalizado : totalizados(liquidacion)) {
                    if (esCandidatoLegacy(totalizado, entrada.getKey(), choferOriginalId, entrada.getValue())) {
                        candidatos.add(new Coincidencia(liquidacion, totalizado));
                    }
                }
            }
            if (candidatos.isEmpty()) {
                algunProductoSinFila = true;
            } else if (candidatos.size() > 1) {
                algunProductoRepetido = true;
            } else {
                coincidencias.add(candidatos.get(0));
            }
        }

        if (algunProductoRepetido) {
            return Rastro.ambiguo();
        }
        if (coincidencias.isEmpty()) {
            return Rastro.ninguno();
        }
        if (algunProductoSinFila) {
            return Rastro.incompleto();
        }

        Set<LiquidacionEntity> liquidacionesTocadas = new LinkedHashSet<>();
        List<LiquidacionTotalizadoEntity> filas = new ArrayList<>();
        for (Coincidencia coincidencia : coincidencias) {
            liquidacionesTocadas.add(coincidencia.liquidacion);
            filas.add(coincidencia.totalizado);
        }
        if (liquidacionesTocadas.size() != 1) {
            return Rastro.ambiguo();
        }
        return Rastro.unico(liquidacionesTocadas.iterator().next(), filas);
    }

    public void incorporarTrayecto(LiquidacionEntity liquidacion, TrayectoEntity viaje) {
        exigirChofer(viaje);
        if (liquidacion.getTotalesPorProductos() == null) {
            liquidacion.setTotalesPorProductos(new LinkedList<>());
        }
        List<LiquidacionTotalizadoEntity> propios = totalizadosDelTrayecto(liquidacion, viaje);
        if (!propios.isEmpty()) {
            reconciliar(liquidacion, viaje, propios);
            return;
        }
        for (Acumulado acumulado : agrupar(viaje).values()) {
            crearTotalizado(liquidacion, viaje, acumulado);
        }
    }

    public void sincronizar(LiquidacionEntity liquidacion,
                            TrayectoEntity viaje,
                            Map<UUID, BigDecimal> anteriores,
                            UUID choferOriginalId) {
        if (liquidacion == null) {
            throw new GenericExeption(MSG_NO_ENCONTRADO);
        }
        if (estaCerrada(liquidacion.getEstadosLiquidacion())) {
            throw new GenericExeption(MSG_CERRADA);
        }
        exigirChofer(viaje);
        if (liquidacion.getTotalesPorProductos() == null) {
            liquidacion.setTotalesPorProductos(new LinkedList<>());
        }

        List<LiquidacionTotalizadoEntity> propios = totalizadosDelTrayecto(liquidacion, viaje);
        if (!propios.isEmpty()) {
            reconciliar(liquidacion, viaje, propios);
            return;
        }

        UUID choferBusqueda = choferOriginalId != null ? choferOriginalId : idEmpleado(viaje.getChofer());
        Rastro rastro = rastrear(List.of(liquidacion), viaje, anteriores, choferBusqueda);
        if (rastro.tipo() == TipoRastro.AMBIGUO) {
            throw new GenericExeption(MSG_AMBIGUO);
        }
        if (rastro.tipo() != TipoRastro.UNICO) {
            throw new GenericExeption(MSG_NO_ENCONTRADO);
        }

        Set<UUID> productosCorregidos = new LinkedHashSet<>();
        Map<UUID, Acumulado> deseados = agrupar(viaje);
        for (LiquidacionTotalizadoEntity totalizado : rastro.coincidencias()) {
            UUID productoId = totalizado.getProductoComercial().getId();
            Acumulado acumulado = deseados.get(productoId);
            if (acumulado == null) {
                liquidacion.removerTotalizado(totalizado);
            } else {
                aplicarAcumulado(totalizado, viaje, acumulado);
                productosCorregidos.add(productoId);
            }
        }
        for (Acumulado acumulado : deseados.values()) {
            if (!productosCorregidos.contains(acumulado.producto.getId())) {
                crearTotalizado(liquidacion, viaje, acumulado);
            }
        }
    }

    private void reconciliar(LiquidacionEntity liquidacion,
                             TrayectoEntity viaje,
                             List<LiquidacionTotalizadoEntity> propios) {
        Map<UUID, Acumulado> deseados = agrupar(viaje);
        Set<UUID> yaActualizados = new LinkedHashSet<>();
        for (LiquidacionTotalizadoEntity totalizado : new ArrayList<>(propios)) {
            UUID productoId = totalizado.getProductoComercial() == null ? null : totalizado.getProductoComercial().getId();
            Acumulado acumulado = productoId == null ? null : deseados.get(productoId);
            if (acumulado == null || !yaActualizados.add(productoId)) {
                liquidacion.removerTotalizado(totalizado);
                continue;
            }
            aplicarAcumulado(totalizado, viaje, acumulado);
        }
        for (Acumulado acumulado : deseados.values()) {
            if (!yaActualizados.contains(acumulado.producto.getId())) {
                crearTotalizado(liquidacion, viaje, acumulado);
            }
        }
    }

    private void crearTotalizado(LiquidacionEntity liquidacion, TrayectoEntity viaje, Acumulado acumulado) {
        LiquidacionTotalizadoEntity totalizado = LiquidacionTotalizadoEntity.builder()
                .productoComercial(acumulado.producto)
                .trayecto(viaje)
                .empleadoTotalizadoLiquidacion(new ArrayList<>())
                .build();
        liquidacion.agregarTotalizado(totalizado);
        aplicarAcumulado(totalizado, viaje, acumulado);
    }

    private void aplicarAcumulado(LiquidacionTotalizadoEntity totalizado, TrayectoEntity viaje, Acumulado acumulado) {
        totalizado.setTrayecto(viaje);
        totalizado.setProductoComercial(acumulado.producto);
        if (totalizado.getEmpleadoTotalizadoLiquidacion() == null) {
            totalizado.setEmpleadoTotalizadoLiquidacion(new ArrayList<>());
        }

        LiquidacionTotalizadoEmpleadoEntity chofer = buscarChofer(totalizado);
        if (chofer == null) {
            chofer = LiquidacionTotalizadoEmpleadoEntity.builder()
                    .empleado(viaje.getChofer())
                    .chofer(true)
                    .cantidad(acumulado.cantidad)
                    .ayudantes(new ArrayList<>())
                    .build();
            chofer.setOrigenTotalizado(totalizado);
            totalizado.getEmpleadoTotalizadoLiquidacion().add(chofer);
        } else {
            chofer.setEmpleado(viaje.getChofer());
            chofer.setChofer(true);
            chofer.setCantidad(acumulado.cantidad);
            if (chofer.getAyudantes() == null) {
                chofer.setAyudantes(new ArrayList<>());
            }
        }

        List<LiquidacionTotalizadoEmpleadoEntity> ayudantes = chofer.getAyudantes();
        ayudantes.removeIf(ayudante -> ayudante.getEmpleado() == null
                || !acumulado.ayudantes.containsKey(ayudante.getEmpleado().getId()));
        for (EmpleadosEntity empleado : acumulado.ayudantes.values()) {
            LiquidacionTotalizadoEmpleadoEntity ayudante = ayudantes.stream()
                    .filter(actual -> empleado.getId().equals(actual.getEmpleado().getId()))
                    .findFirst()
                    .orElse(null);
            if (ayudante == null) {
                ayudante = LiquidacionTotalizadoEmpleadoEntity.builder()
                        .empleado(empleado)
                        .chofer(false)
                        .cantidad(acumulado.cantidad)
                        .ayudantes(new ArrayList<>())
                        .build();
                ayudante.setOrigenTotalizado(totalizado);
                chofer.addAyudante(ayudante);
            } else {
                ayudante.setCantidad(acumulado.cantidad);
                ayudante.setChofer(false);
            }
        }
    }

    private Map<UUID, Acumulado> agrupar(TrayectoEntity viaje) {
        Map<UUID, Acumulado> acumulados = new LinkedHashMap<>();
        if (viaje == null || viaje.getVentaTrayecto() == null) {
            return acumulados;
        }
        for (VentaTrayectoEntity venta : viaje.getVentaTrayecto()) {
            if (venta.getProducto() == null || venta.getProducto().getId() == null) {
                log.warn("Venta {} del trayecto {} sin producto. No se envía a liquidación.",
                        venta.getIdVenta(), viaje.getIdTrayecto());
                continue;
            }
            if (venta.getCantidadSalida() == null || venta.getCantidadLlegada() == null) {
                log.warn("Venta {} (producto {}) con cantidades nulas al liquidar trayecto {}: salida={}, llegada={}. Se usó 0.",
                        venta.getIdVenta(),
                        venta.getProducto().getId(),
                        viaje.getIdTrayecto(),
                        venta.getCantidadSalida(),
                        venta.getCantidadLlegada());
            }
            Acumulado acumulado = acumulados.computeIfAbsent(venta.getProducto().getId(), id -> new Acumulado(venta.getProducto()));
            acumulado.cantidad = acumulado.cantidad.add(cantidadVendida(venta));
            if (venta.getComisionesEmpleados() == null) {
                continue;
            }
            for (VentasEmpleadoEntity comision : venta.getComisionesEmpleados()) {
                if (comision.getEmpleado() != null && comision.getEmpleado().getId() != null) {
                    acumulado.ayudantes.putIfAbsent(comision.getEmpleado().getId(), comision.getEmpleado());
                }
            }
        }
        return acumulados;
    }

    private boolean tieneVinculo(LiquidacionEntity liquidacion, TrayectoEntity viaje) {
        if (viaje == null || viaje.getIdTrayecto() == null) {
            return false;
        }
        return totalizados(liquidacion).stream().anyMatch(totalizado -> perteneceAlTrayecto(totalizado, viaje.getIdTrayecto()));
    }

    private List<LiquidacionTotalizadoEntity> totalizadosDelTrayecto(LiquidacionEntity liquidacion, TrayectoEntity viaje) {
        if (viaje.getIdTrayecto() == null) {
            return List.of();
        }
        return totalizados(liquidacion).stream()
                .filter(totalizado -> perteneceAlTrayecto(totalizado, viaje.getIdTrayecto()))
                .toList();
    }

    private boolean perteneceAlTrayecto(LiquidacionTotalizadoEntity totalizado, UUID trayectoId) {
        return totalizado.getTrayecto() != null && trayectoId.equals(totalizado.getTrayecto().getIdTrayecto());
    }

    private boolean esCandidatoLegacy(LiquidacionTotalizadoEntity totalizado,
                                     UUID productoId,
                                     UUID choferId,
                                     BigDecimal cantidad) {
        if (totalizado.getTrayecto() != null || totalizado.getProductoComercial() == null) {
            return false;
        }
        if (!productoId.equals(totalizado.getProductoComercial().getId())) {
            return false;
        }
        LiquidacionTotalizadoEmpleadoEntity chofer = buscarChofer(totalizado);
        return chofer != null
                && chofer.getEmpleado() != null
                && choferId.equals(chofer.getEmpleado().getId())
                && mismaCantidad(chofer.getCantidad(), cantidad);
    }

    private LiquidacionTotalizadoEmpleadoEntity buscarChofer(LiquidacionTotalizadoEntity totalizado) {
        if (totalizado.getEmpleadoTotalizadoLiquidacion() == null) {
            return null;
        }
        return totalizado.getEmpleadoTotalizadoLiquidacion().stream()
                .filter(empleado -> Boolean.TRUE.equals(empleado.getChofer()))
                .findFirst()
                .orElse(null);
    }

    private List<LiquidacionTotalizadoEntity> totalizados(LiquidacionEntity liquidacion) {
        if (liquidacion == null || liquidacion.getTotalesPorProductos() == null) {
            return List.of();
        }
        return liquidacion.getTotalesPorProductos();
    }

    private void exigirChofer(TrayectoEntity viaje) {
        if (viaje == null || viaje.getChofer() == null) {
            throw new IllegalArgumentException("El viaje debe tener un chofer asignado");
        }
    }

    private UUID idEmpleado(EmpleadosEntity empleado) {
        return empleado == null ? null : empleado.getId();
    }

    private BigDecimal cantidadVendida(VentaTrayectoEntity venta) {
        BigDecimal salida = venta.getCantidadSalida() != null ? venta.getCantidadSalida() : BigDecimal.ZERO;
        BigDecimal llegada = venta.getCantidadLlegada() != null ? venta.getCantidadLlegada() : BigDecimal.ZERO;
        return salida.subtract(llegada);
    }

    private boolean mismaCantidad(BigDecimal izquierda, BigDecimal derecha) {
        BigDecimal a = izquierda != null ? izquierda : BigDecimal.ZERO;
        BigDecimal b = derecha != null ? derecha : BigDecimal.ZERO;
        return a.compareTo(b) == 0;
    }

    public boolean estaCerrada(EstadosLiquidacion estado) {
        return EstadosLiquidacion.FINALIZADA.equals(estado) || EstadosLiquidacion.CANCELADA.equals(estado);
    }

    public enum TipoRastro {
        NINGUNO,
        VINCULADO,
        UNICO,
        AMBIGUO,
        INCOMPLETO
    }

    public record Rastro(TipoRastro tipo, LiquidacionEntity liquidacion, List<LiquidacionTotalizadoEntity> coincidencias) {
        public static Rastro ninguno() {
            return new Rastro(TipoRastro.NINGUNO, null, List.of());
        }

        public static Rastro ambiguo() {
            return new Rastro(TipoRastro.AMBIGUO, null, List.of());
        }

        public static Rastro incompleto() {
            return new Rastro(TipoRastro.INCOMPLETO, null, List.of());
        }

        public static Rastro vinculado(LiquidacionEntity liquidacion) {
            return new Rastro(TipoRastro.VINCULADO, liquidacion, List.of());
        }

        public static Rastro unico(LiquidacionEntity liquidacion, List<LiquidacionTotalizadoEntity> coincidencias) {
            return new Rastro(TipoRastro.UNICO, liquidacion, coincidencias);
        }

        public boolean yaEnviado() {
            return tipo == TipoRastro.VINCULADO
                    || tipo == TipoRastro.UNICO
                    || tipo == TipoRastro.AMBIGUO
                    || tipo == TipoRastro.INCOMPLETO;
        }
    }

    private record Coincidencia(LiquidacionEntity liquidacion, LiquidacionTotalizadoEntity totalizado) {
    }

    private static final class Acumulado {
        private final ProductoComercialEntity producto;
        private BigDecimal cantidad = BigDecimal.ZERO;
        private final Map<UUID, EmpleadosEntity> ayudantes = new LinkedHashMap<>();

        private Acumulado(ProductoComercialEntity producto) {
            this.producto = producto;
        }
    }
}
