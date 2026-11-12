package com.main.tancol.reportes.service;

import com.main.tancol.empleados.entities.EmpleadosEntity;
import com.main.tancol.metaventa.entities.MetaVentaEmpleadoEntity;
import com.main.tancol.metaventa.repositories.MetaVentaEmpleadoRepository;
import com.main.tancol.metaventa.util.MetaVentaValueMapper;
import com.main.tancol.producto.entities.comerciales.ProductoComercialEntity;
import com.main.tancol.liquidacion.dto.VentaEmpleadoProductoAgg;
import com.main.tancol.liquidacion.repositories.EmpleadoTotalizadoLiquidacionRepository;
import com.main.tancol.reportes.dto.request.ReporteVentasEmpleadosMetasRequestDTO;
import com.main.tancol.reportes.dto.response.ReporteVentasEmpleadosMetasResponseDTO;
import com.main.tancol.tablascomisiones.entities.TablasComisionesEntity;
import com.main.tancol.tablascomisiones.repositories.TablasComisionesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReporteVentasEmpleadosMetasService {

    static final int DIAS_MAXIMOS = 8;

    private final EmpleadoTotalizadoLiquidacionRepository ventasRepository;
    private final MetaVentaEmpleadoRepository metaRepository;
    private final TablasComisionesRepository comisionesRepository;

    @Transactional(readOnly = true)
    public List<ReporteVentasEmpleadosMetasResponseDTO> generar(ReporteVentasEmpleadosMetasRequestDTO filtro) {
        validar(filtro);
        List<UUID> productos = filtro.getIdProductos().stream().filter(Objects::nonNull).distinct().toList();
        LocalDateTime inicio = filtro.getFechaInicio();
        LocalDateTime fin = filtro.getFechaFin();

        Map<Clave, MetaVentaEmpleadoEntity> metas = new HashMap<>();
        for (MetaVentaEmpleadoEntity meta : metaRepository.findVigentesPorProductos(productos, inicio, fin)) {
            if (meta.getEmpleado() == null || meta.getEmpleado().getId() == null
                    || meta.getProducto() == null || meta.getProducto().getId() == null) {
                continue;
            }
            Clave clave = new Clave(meta.getEmpleado().getId(), meta.getProducto().getId());
            metas.merge(clave, meta, ReporteVentasEmpleadosMetasService::preferirMeta);
        }

        Map<UUID, BigDecimal> comisionPorProducto = new HashMap<>();
        for (TablasComisionesEntity tabla : comisionesRepository.findByProductoIdInAndDeletedFalse(productos)) {
            if (tabla.getProducto() == null || tabla.getProducto().getId() == null) {
                continue;
            }
            comisionPorProducto.putIfAbsent(tabla.getProducto().getId(), tabla.getComision());
        }

        Map<Clave, VentaEmpleadoProductoAgg> ventas = new HashMap<>();
        for (VentaEmpleadoProductoAgg venta : ventasRepository.sumarVentasPorEmpleadoYProducto(productos, inicio, fin)) {
            if (venta.getEmpleadoId() == null || venta.getProductoId() == null) {
                continue;
            }
            ventas.put(new Clave(venta.getEmpleadoId(), venta.getProductoId()), venta);
        }

        Set<Clave> claves = new LinkedHashSet<>();
        claves.addAll(metas.keySet());
        claves.addAll(ventas.keySet());

        List<ReporteVentasEmpleadosMetasResponseDTO> filas = new ArrayList<>();
        for (Clave clave : claves) {
            filas.add(fila(clave, metas.get(clave), ventas.get(clave), comisionPorProducto.get(clave.productoId())));
        }
        filas.sort(Comparator
                .comparing(ReporteVentasEmpleadosMetasResponseDTO::getProductoNombre,
                        Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER))
                .thenComparing(ReporteVentasEmpleadosMetasResponseDTO::getEmpleadoNombre,
                        Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)));
        return filas;
    }

    static void validar(ReporteVentasEmpleadosMetasRequestDTO filtro) {
        if (filtro == null || filtro.getFechaInicio() == null || filtro.getFechaFin() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "fechaInicio y fechaFin son obligatorias");
        }
        if (!filtro.getFechaFin().isAfter(filtro.getFechaInicio())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "fechaFin debe ser posterior a fechaInicio");
        }
        if (Duration.between(filtro.getFechaInicio(), filtro.getFechaFin()).compareTo(Duration.ofDays(DIAS_MAXIMOS)) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El rango de fechas no puede superar 8 días");
        }
        if (filtro.getIdProductos() == null || filtro.getIdProductos().stream().noneMatch(Objects::nonNull)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe indicar al menos un producto");
        }
    }

    static BigDecimal alcance(BigDecimal ventaSemana, BigDecimal ventaMinima) {
        if (ventaMinima == null) {
            return null;
        }
        BigDecimal vendido = ventaSemana == null ? BigDecimal.ZERO : ventaSemana;
        return vendido.subtract(ventaMinima);
    }

    static BigDecimal comisionSugerida(BigDecimal alcanceObjetivo, BigDecimal comisionTabla) {
        if (alcanceObjetivo == null || comisionTabla == null) {
            return null;
        }
        return alcanceObjetivo.multiply(comisionTabla).setScale(2, RoundingMode.HALF_UP);
    }

    private static ReporteVentasEmpleadosMetasResponseDTO fila(Clave clave,
                                                               MetaVentaEmpleadoEntity meta,
                                                               VentaEmpleadoProductoAgg venta,
                                                               BigDecimal comisionTabla) {
        BigDecimal ventaSemana = venta == null || venta.getCantidad() == null ? BigDecimal.ZERO : venta.getCantidad();
        BigDecimal ventaMinima = meta == null ? null : meta.getCantidadMinima();
        BigDecimal alcanceObjetivo = alcance(ventaSemana, ventaMinima);
        ProductoComercialEntity producto = meta == null ? null : meta.getProducto();
        return ReporteVentasEmpleadosMetasResponseDTO.builder()
                .empleadoId(clave.empleadoId())
                .empleadoNombre(nombreEmpleado(meta, venta))
                .productoId(clave.productoId())
                .productoNombre(producto != null ? producto.getNombre() : venta.getProductoNombre())
                .productoAlias(producto != null ? producto.getAlias() : venta.getProductoAlias())
                .ventaMinima(ventaMinima)
                .ventaSemana(ventaSemana)
                .alcanceObjetivo(alcanceObjetivo)
                .comisionSugerida(comisionSugerida(alcanceObjetivo, comisionTabla))
                .build();
    }

    private static String nombreEmpleado(MetaVentaEmpleadoEntity meta, VentaEmpleadoProductoAgg venta) {
        if (meta != null && meta.getEmpleado() != null) {
            return MetaVentaValueMapper.nombreEmpleado(meta.getEmpleado());
        }
        if (venta == null) {
            return null;
        }
        EmpleadosEntity empleado = new EmpleadosEntity();
        empleado.setNombre(venta.getNombre());
        empleado.setPrimerApellido(venta.getPrimerApellido());
        empleado.setSegundoApellido(venta.getSegundoApellido());
        return MetaVentaValueMapper.nombreEmpleado(empleado);
    }

    private static MetaVentaEmpleadoEntity preferirMeta(MetaVentaEmpleadoEntity actual,
                                                        MetaVentaEmpleadoEntity candidata) {
        if (candidata.isActiva() != actual.isActiva()) {
            return candidata.isActiva() ? candidata : actual;
        }
        LocalDateTime inicioActual = actual.getFechaInicio();
        LocalDateTime inicioCandidata = candidata.getFechaInicio();
        if (inicioCandidata == null) {
            return actual;
        }
        if (inicioActual == null || inicioCandidata.isAfter(inicioActual)) {
            return candidata;
        }
        return actual;
    }

    private record Clave(UUID empleadoId, UUID productoId) {
    }
}
