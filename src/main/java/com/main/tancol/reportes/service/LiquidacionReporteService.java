package com.main.tancol.reportes.service;

import com.main.tancol.liquidacion.enums.FormaPagoLiquidacion;
import com.main.tancol.reportes.dto.response.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class LiquidacionReporteService {

    @PersistenceContext
    private final EntityManager entityManager;

    public List<ReporteLiquidacionesResponseDTO> getLiquidaciones(List<UUID> clientes, List<UUID> rutas, List<UUID> productos, LocalDateTime fechaInicio, LocalDateTime fechaFin
            , List<FormaPagoLiquidacion> tipoPago, List<UUID> conceptosVenta, Boolean facturado, String estadoLiquidado, String x) {

        Query query = entityManager.createNativeQuery(
                "SELECT * FROM get_liquidaciones_partidas(:clientes,:rutas,:productos,:fechaInicio,:fechaFin,:conceptosVenta,:facturado,:tiposPago,:estadoLiquidado,:x)");
        query.setParameter("clientes", clientes != null && !clientes.isEmpty() ? clientes.toArray(UUID[]::new) : null);
        query.setParameter("rutas", rutas != null && !rutas.isEmpty() ? rutas.toArray(UUID[]::new) : null);
        query.setParameter("productos", productos != null && !productos.isEmpty() ? productos.toArray(UUID[]::new) : null);
        query.setParameter("fechaInicio", fechaInicio);
        query.setParameter("fechaFin", fechaFin);
        query.setParameter("conceptosVenta", conceptosVenta != null && !conceptosVenta.isEmpty() ? conceptosVenta.toArray(UUID[]::new) : null);
        query.setParameter("facturado", facturado);
        query.setParameter("tiposPago", tipoPago != null && !tipoPago.isEmpty() ? tipoPago.stream().map(FormaPagoLiquidacion::toString).toArray(String[]::new) : null);
        query.setParameter("estadoLiquidado", estadoLiquidado);
        query.setParameter("x", x);

        List<Object[]> resultado = query.getResultList();

        return resultado.stream().map(row -> new ReporteLiquidacionesResponseDTO(
                (String) row[0],   // folio
                (String) row[1],   // bloque_nota
                toLong(row[2]),    // folio_nota
                (String) row[3],   // nombre_ruta
                (String) row[4],   // nombre_producto
                (BigDecimal) row[5],   // cantidad
                (BigDecimal) row[6],   // precio
                (BigDecimal) row[7],   // descuento_real
                (BigDecimal) row[8],   // importe
                toBigDecimal(row[9]),   // cuentaa (solo valor si x = "xyz")
                toBigDecimal(row[10]),  // cuentab (solo valor si x = "xyz")
                (String) row[11],   // nombre_cliente
                (String) row[12],   // concepto_venta
                (Boolean) row[13],   // f1 (is_exclude)
                (Boolean) row[14],   // descuento
                (Boolean) row[15],   // cancelados
                (Boolean) row[16],   // facturados
                (String) row[17],   // tipo_pago
                (String) row[18],   // estado_liquidacion
                (String) row[19],   // vendedor_chofer
                (String) row[20],   // ayudantes
                convertirALocalTime(row[21])   // fecha
        )).toList();

    }

    public List<ReporteLiquidacionesCuentaResponseDTO> getLiquidacionConCuentas(List<UUID> clientes, List<UUID> rutas, List<UUID> productos, LocalDateTime fechaInicio, LocalDateTime fechaFin
            , List<FormaPagoLiquidacion> tipoPago, List<UUID> conceptosVenta, String estadoLiquidado) {
        Query query = entityManager.createNativeQuery(
                "SELECT * FROM get_liquidaciones_partidas_cuentas(:clientes,:rutas,:productos,:fechaInicio,:fechaFin,:conceptosVenta,:tiposPago, :estadoLiquidado)");
        query.setParameter("clientes", clientes != null && !clientes.isEmpty() ? clientes.toArray(UUID[]::new) : null);
        query.setParameter("rutas", rutas != null && !rutas.isEmpty() ? rutas.toArray(UUID[]::new) : null);
        query.setParameter("productos", productos != null && !productos.isEmpty() ? productos.toArray(UUID[]::new) : null);
        query.setParameter("fechaInicio", fechaInicio);
        query.setParameter("fechaFin", fechaFin);
        query.setParameter("conceptosVenta", conceptosVenta != null && !conceptosVenta.isEmpty() ? conceptosVenta.toArray(UUID[]::new) : null);
        query.setParameter("tiposPago", tipoPago != null && !tipoPago.isEmpty() ? tipoPago.stream().map(FormaPagoLiquidacion::toString).toArray(String[]::new) : null);
        query.setParameter("estadoLiquidado", estadoLiquidado);


        List<Object[]> resultado = query.getResultList();

//        for (Object[] row : resultado) {
//            for (int i = 0; i < row.length; i++) {
//                System.out.println("Index " + i + " -> " + row[i] + " | Tipo: " + row[i].getClass());
//            }
//        }

        return resultado.stream().map(row -> new ReporteLiquidacionesCuentaResponseDTO(
                (String) row[0],
                (String) row[1],
                toLong(row[2]),
                (String) row[3],
                (String) row[4],
                (BigDecimal) row[5],
                (BigDecimal) row[6],
                (BigDecimal) row[7],
                (BigDecimal) row[8],
                (BigDecimal) row[10],
                (BigDecimal) row[9],
                (String) row[11], //nombre_cliente
                (String) row[12], //concepto_venta
                (Boolean) row[13], // f1
                (Boolean) row[14], // descuento
                (Boolean) row[15], // cancel
                (String) row[16], // tipo_pago
                (String) row[17], //estado_liquidacion
                (String) row[18], // vendedor_chofer
                (String) row[19], //ayudantes
                convertirALocalTime(row[20]) // fecha
        )).toList();

    }

    private BigDecimal toBigDecimal(Object value) {
        if (value == null) return BigDecimal.ZERO;
        if (value instanceof BigDecimal bd) return bd;
        if (value instanceof Number n) return new BigDecimal(n.toString());
        if (value instanceof String s && !s.isBlank()) return new BigDecimal(s.trim());
        return BigDecimal.ZERO;
    }

    private Long toLong(Object value) {
        if (value == null) return null;
        if (value instanceof Long l) return l;
        if (value instanceof Number n) return n.longValue();
        if (value instanceof String s && !s.isBlank()) return Long.valueOf(s.trim());
        return null;
    }


    public List<ReporteVolumenVentasResponseDTO> getVolumenPorEmpleado(List<UUID> empleados, List<UUID> productos, List<UUID> rutas, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        Query query = entityManager.createNativeQuery(
                "SELECT * FROM get_ventas_empleados(:empleados,:productos,:rutas,:fechaInicio,:fechaFin)");
        query.setParameter("empleados", empleados != null && !empleados.isEmpty() ? empleados.toArray(UUID[]::new) : null);
        query.setParameter("productos", productos != null && !productos.isEmpty() ? productos.toArray(UUID[]::new) : null);
        query.setParameter("rutas", rutas != null && !rutas.isEmpty() ? rutas.toArray(UUID[]::new) : null);
        query.setParameter("fechaInicio", fechaInicio);
        query.setParameter("fechaFin", fechaFin);

        List<Object[]> resultado = query.getResultList();
        return resultado.stream().map(row -> new ReporteVolumenVentasResponseDTO(
                (Long) row[0],     // id_empleado
                (String) row[1],     // nombre
                (String) row[2],     // producto
                (BigDecimal) row[3]       // cantidad_vendida
        )).toList();

    }

    public List<ReporteVVEDiaResponseDTO> getVolumenPorEmpleadoDia(List<UUID> empleados,
                                                                  List<UUID> productos,
                                                                  List<UUID> rutas,
                                                                  List<UUID> conceptosVenta,
                                                                  Boolean facturado,
                                                                  LocalDateTime fechaInicio,
                                                                  LocalDateTime fechaFin) {
        Query query = entityManager.createNativeQuery(
                "SELECT * FROM get_ventas_empleados_dia(:empleados,:productos,:rutas,:conceptosVenta,:facturado,:fechaInicio,:fechaFin)");
        query.setParameter("empleados", empleados != null && !empleados.isEmpty() ? empleados.toArray(UUID[]::new) : null);
        query.setParameter("productos", productos != null && !productos.isEmpty() ? productos.toArray(UUID[]::new) : null);
        query.setParameter("rutas", rutas != null && !rutas.isEmpty() ? rutas.toArray(UUID[]::new) : null);
        query.setParameter("conceptosVenta", conceptosVenta != null && !conceptosVenta.isEmpty() ? conceptosVenta.toArray(UUID[]::new) : null);
        query.setParameter("facturado", facturado);
        query.setParameter("fechaInicio", fechaInicio);
        query.setParameter("fechaFin", fechaFin);

        List<Object[]> resultado = query.getResultList();
        return resultado.stream().map(row -> new ReporteVVEDiaResponseDTO(
                (Date) row[0],      // fecha DD-MM-AA
                (Long) row[1],     // id_empleado
                (String) row[2],     // nombre
                (String) row[3],     // producto
                (BigDecimal) row[4],    // cantidad_vendida
                (BigDecimal) row[5]     //Importe
        )).toList();

    }


    public List<ReporteTotalizadoClienteResponseDTO> getVolumenPorCliente(Set<UUID> clientes, Set<UUID> productos, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        Query query = entityManager.createNativeQuery(
                "SELECT * FROM get_ventas_clientes(:clientes,:productos,:fechaInicio,:fechaFin)");
        query.setParameter("clientes", clientes != null && !clientes.isEmpty() ? clientes.toArray(UUID[]::new) : null);
        query.setParameter("productos", productos != null && !productos.isEmpty() ? productos.toArray(UUID[]::new) : null);
        query.setParameter("fechaInicio", fechaInicio);
        query.setParameter("fechaFin", fechaFin);

        List<Object[]> resultado = query.getResultList();
        return resultado.stream().map(row -> new ReporteTotalizadoClienteResponseDTO(
                (String) row[0],     // alias.
                (String) row[1],     // nombre.
                (String) row[2],     // nombre_comercial.
                (String) row[3],       // razon_social.
                (BigDecimal) row[4]       // cantidad_vendida.
        )).toList();

    }


    private LocalDateTime convertirALocalTime(Object row) {
        Timestamp timestamp = (Timestamp) row;

        return timestamp != null ? timestamp.toLocalDateTime() : null;
    }


}
