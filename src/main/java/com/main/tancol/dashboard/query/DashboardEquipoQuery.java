package com.main.tancol.dashboard.query;

import com.main.tancol.dashboard.dto.request.DashboardKpiFilter;
import com.main.tancol.dashboard.dto.response.CantidadPorDiaResponseDTO;
import com.main.tancol.dashboard.dto.response.ClienteMasEquiposResponseDTO;
import com.main.tancol.dashboard.dto.response.DevolucionesEquiposResponseDTO;
import com.main.tancol.dashboard.dto.response.MovimientoEquiposDiaResponseDTO;
import com.main.tancol.dashboard.dto.response.TipoEquipoCantidadResponseDTO;
import com.main.tancol.dashboard.util.DashboardNativeTypes;
import com.main.tancol.dashboard.util.DashboardPeriodo;
import com.main.tancol.dashboard.util.NativeQueryBuilder;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DashboardEquipoQuery {

    @PersistenceContext
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
    public List<ClienteMasEquiposResponseDTO> clientesConMasEquipos(DashboardKpiFilter filter, DashboardPeriodo periodo) {
        DashboardPeriodo rango = periodo != null
                ? periodo
                : DashboardPeriodo.of(DashboardPeriodo.hoyMexico(), DashboardPeriodo.hoyMexico());
        LocalDateTime corte = rango.finExclusivo();
        boolean sinPeriodo = filter.getFechaInicio() == null && filter.getFechaFin() == null;
        LocalDateTime inicioPeriodo = sinPeriodo ? corte : rango.inicio();

        NativeQueryBuilder sql = new NativeQueryBuilder()
                .append("""
                        SELECT c.id,
                               c.nombre_comercial,
                               COUNT(*) FILTER (
                                   WHERE ea.fecha_asignacion < :corte
                                     AND (ea.fecha_devolucion IS NULL OR ea.fecha_devolucion >= :corte)
                               ) AS equipos_activos,
                               COUNT(*) FILTER (
                                   WHERE ea.fecha_devolucion >= :inicioPeriodo
                                     AND ea.fecha_devolucion < :corte
                               ) AS equipos_devueltos,
                               MAX(ea.fecha_asignacion) FILTER (
                                   WHERE ea.fecha_asignacion < :corte
                                     AND (ea.fecha_devolucion IS NULL OR ea.fecha_devolucion >= :corte)
                               ) AS ultima_asignacion
                        FROM equipo_asignaciones ea
                                 INNER JOIN clientes c ON c.id = ea.cliente_id
                                 LEFT JOIN equipos e ON e.id = ea.equipo_id
                        WHERE COALESCE(ea.deleted, FALSE) = FALSE
                        """)
                .param("corte", corte)
                .param("inicioPeriodo", inicioPeriodo);
        applyEquipoFilters(sql, filter);
        applyClienteEstado(sql, filter);
        sql.append("""
                 GROUP BY c.id, c.nombre_comercial
                 HAVING COUNT(*) FILTER (
                     WHERE ea.fecha_asignacion < :corte
                       AND (ea.fecha_devolucion IS NULL OR ea.fecha_devolucion >= :corte)
                 ) > 0
                 ORDER BY equipos_activos DESC, c.nombre_comercial
                """);

        var query = sql.create(entityManager);
        query.setMaxResults(filter.getLimit());
        List<Object[]> rows = query.getResultList();
        return rows.stream().map(row -> {
            int activos = DashboardNativeTypes.toInt(row[2]);
            return ClienteMasEquiposResponseDTO.builder()
                    .idCliente(DashboardNativeTypes.toUuid(row[0]))
                    .cliente(DashboardNativeTypes.toStringValue(row[1]))
                    .equiposAsignados(activos)
                    .equiposActivos(activos)
                    .equiposDevueltosEnPeriodo(DashboardNativeTypes.toInt(row[3]))
                    .ultimaAsignacion(DashboardNativeTypes.toLocalDate(row[4]))
                    .build();
        }).toList();
    }

    public DevolucionesEquiposResponseDTO devoluciones(DashboardKpiFilter filter, DashboardPeriodo periodo) {
        Object[] totales = totalesDevoluciones(filter, periodo);
        DevolucionesEquiposResponseDTO response = DevolucionesEquiposResponseDTO.builder()
                .totalDevoluciones(DashboardNativeTypes.toInt(totales[0]))
                .equiposDevueltos(DashboardNativeTypes.toInt(totales[1]))
                .clientesConDevoluciones(DashboardNativeTypes.toInt(totales[2]))
                .porTipoEquipo(devolucionesPorTipo(filter, periodo))
                .porDia(devolucionesPorDia(filter, periodo))
                .build();
        if (response.getPorTipoEquipo() == null) {
            response.setPorTipoEquipo(new ArrayList<>());
        }
        if (response.getPorDia() == null) {
            response.setPorDia(new ArrayList<>());
        }
        return response;
    }

    @SuppressWarnings("unchecked")
    public List<MovimientoEquiposDiaResponseDTO> movimientoPorDia(DashboardKpiFilter filter, DashboardPeriodo periodo) {
        NativeQueryBuilder asignaciones = new NativeQueryBuilder()
                .append("""
                        SELECT CAST(ea.fecha_asignacion AS date) AS fecha,
                               COUNT(*) AS cantidad
                        FROM equipo_asignaciones ea
                                 INNER JOIN clientes c ON c.id = ea.cliente_id
                                 INNER JOIN equipos e ON e.id = ea.equipo_id
                        WHERE COALESCE(ea.deleted, FALSE) = FALSE
                          AND ea.fecha_asignacion >= :fechaInicio
                          AND ea.fecha_asignacion < :fechaFinExclusivo
                        """)
                .param("fechaInicio", periodo.inicio())
                .param("fechaFinExclusivo", periodo.finExclusivo());
        applyEquipoFilters(asignaciones, filter);
        applyClienteEstado(asignaciones, filter);
        asignaciones.append(" GROUP BY CAST(ea.fecha_asignacion AS date)");

        NativeQueryBuilder devoluciones = new NativeQueryBuilder()
                .append("""
                        SELECT CAST(ea.fecha_devolucion AS date) AS fecha,
                               COUNT(*) AS cantidad
                        FROM equipo_asignaciones ea
                                 INNER JOIN clientes c ON c.id = ea.cliente_id
                                 INNER JOIN equipos e ON e.id = ea.equipo_id
                        WHERE COALESCE(ea.deleted, FALSE) = FALSE
                          AND ea.activa = FALSE
                          AND ea.fecha_devolucion >= :fechaInicio
                          AND ea.fecha_devolucion < :fechaFinExclusivo
                        """)
                .param("fechaInicio", periodo.inicio())
                .param("fechaFinExclusivo", periodo.finExclusivo());
        applyEquipoFilters(devoluciones, filter);
        applyClienteEstado(devoluciones, filter);
        devoluciones.append(" GROUP BY CAST(ea.fecha_devolucion AS date)");

        Map<java.time.LocalDate, int[]> porDia = new HashMap<>();
        List<Object[]> asignacionRows = asignaciones.create(entityManager).getResultList();
        for (Object[] row : asignacionRows) {
            java.time.LocalDate fecha = DashboardNativeTypes.toLocalDate(row[0]);
            porDia.computeIfAbsent(fecha, key -> new int[2])[0] = DashboardNativeTypes.toInt(row[1]);
        }
        List<Object[]> devolucionRows = devoluciones.create(entityManager).getResultList();
        for (Object[] row : devolucionRows) {
            java.time.LocalDate fecha = DashboardNativeTypes.toLocalDate(row[0]);
            porDia.computeIfAbsent(fecha, key -> new int[2])[1] = DashboardNativeTypes.toInt(row[1]);
        }

        return porDia.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> {
                    int asignados = entry.getValue()[0];
                    int devueltos = entry.getValue()[1];
                    return MovimientoEquiposDiaResponseDTO.builder()
                            .fecha(entry.getKey())
                            .asignaciones(asignados)
                            .devoluciones(devueltos)
                            .movimientosTotales(asignados + devueltos)
                            .build();
                })
                .toList();
    }

    @SuppressWarnings("unchecked")
    private Object[] totalesDevoluciones(DashboardKpiFilter filter, DashboardPeriodo periodo) {
        NativeQueryBuilder sql = baseDevoluciones("""
                SELECT COUNT(*) AS total_devoluciones,
                       COUNT(DISTINCT ea.equipo_id) AS equipos_devueltos,
                       COUNT(DISTINCT ea.cliente_id) AS clientes_con_devoluciones
                """, filter, periodo);
        List<Object[]> rows = sql.create(entityManager).getResultList();
        if (rows.isEmpty() || rows.get(0) == null) {
            return new Object[]{0, 0, 0};
        }
        return rows.get(0);
    }

    @SuppressWarnings("unchecked")
    private List<TipoEquipoCantidadResponseDTO> devolucionesPorTipo(DashboardKpiFilter filter, DashboardPeriodo periodo) {
        NativeQueryBuilder sql = baseDevoluciones("""
                SELECT te.id,
                       te.nombre,
                       COUNT(*) AS cantidad
                """, filter, periodo);
        sql.append(" GROUP BY te.id, te.nombre ORDER BY cantidad DESC, te.nombre");
        List<Object[]> rows = sql.create(entityManager).getResultList();
        return rows.stream().map(row -> TipoEquipoCantidadResponseDTO.builder()
                .idTipoEquipo(DashboardNativeTypes.toUuid(row[0]))
                .tipoEquipo(DashboardNativeTypes.toStringValue(row[1]))
                .cantidad(DashboardNativeTypes.toInt(row[2]))
                .build()
        ).toList();
    }

    @SuppressWarnings("unchecked")
    private List<CantidadPorDiaResponseDTO> devolucionesPorDia(DashboardKpiFilter filter, DashboardPeriodo periodo) {
        NativeQueryBuilder sql = baseDevoluciones("""
                SELECT CAST(ea.fecha_devolucion AS date) AS fecha,
                       COUNT(*) AS cantidad
                """, filter, periodo);
        sql.append(" GROUP BY CAST(ea.fecha_devolucion AS date) ORDER BY fecha");
        List<Object[]> rows = sql.create(entityManager).getResultList();
        return rows.stream().map(row -> CantidadPorDiaResponseDTO.builder()
                .fecha(DashboardNativeTypes.toLocalDate(row[0]))
                .cantidad(DashboardNativeTypes.toInt(row[1]))
                .build()
        ).toList();
    }

    private NativeQueryBuilder baseDevoluciones(String select, DashboardKpiFilter filter, DashboardPeriodo periodo) {
        NativeQueryBuilder sql = new NativeQueryBuilder()
                .append(select)
                .append("""
                         FROM equipo_asignaciones ea
                                  INNER JOIN clientes c ON c.id = ea.cliente_id
                                  INNER JOIN equipos e ON e.id = ea.equipo_id
                                  LEFT JOIN tipos_equipo te ON te.id = e.tipo_equipo_id
                         WHERE COALESCE(ea.deleted, FALSE) = FALSE
                           AND ea.activa = FALSE
                           AND ea.fecha_devolucion >= :fechaInicio
                           AND ea.fecha_devolucion < :fechaFinExclusivo
                        """)
                .param("fechaInicio", periodo.inicio())
                .param("fechaFinExclusivo", periodo.finExclusivo());
        applyEquipoFilters(sql, filter);
        applyClienteEstado(sql, filter);
        return sql;
    }

    private void applyEquipoFilters(NativeQueryBuilder sql, DashboardKpiFilter filter) {
        if (DashboardNativeTypes.hasValues(filter.getIdClientes())) {
            sql.append(" AND ea.cliente_id IN (:idClientes)").param("idClientes", filter.getIdClientes());
        }
        if (DashboardNativeTypes.hasValues(filter.getIdRutas())) {
            sql.append(" AND c.ruta_asociada_id IN (:idRutas)").param("idRutas", filter.getIdRutas());
        }
        if (DashboardNativeTypes.hasValues(filter.getIdEquipos())) {
            sql.append(" AND ea.equipo_id IN (:idEquipos)").param("idEquipos", filter.getIdEquipos());
        }
        if (DashboardNativeTypes.hasValues(filter.getIdTiposEquipo())) {
            sql.append(" AND e.tipo_equipo_id IN (:idTiposEquipo)").param("idTiposEquipo", filter.getIdTiposEquipo());
        }
    }

    private void applyClienteEstado(NativeQueryBuilder sql, DashboardKpiFilter filter) {
        if (!filter.isIncluirClientesInactivos()) {
            sql.append(" AND COALESCE(c.enabled, TRUE) = TRUE AND COALESCE(c.deleted, FALSE) = FALSE");
        }
    }
}
