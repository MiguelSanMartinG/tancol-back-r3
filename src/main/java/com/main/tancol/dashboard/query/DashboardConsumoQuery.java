package com.main.tancol.dashboard.query;

import com.main.tancol.dashboard.config.DashboardKpiProperties;
import com.main.tancol.dashboard.dto.request.DashboardKpiFilter;
import com.main.tancol.dashboard.dto.response.ClienteSinConsumoResponseDTO;
import com.main.tancol.dashboard.dto.response.ConsumoClientesPorDiaResponseDTO;
import com.main.tancol.dashboard.dto.response.TopClientePgExpResponseDTO;
import com.main.tancol.dashboard.util.DashboardNativeTypes;
import com.main.tancol.dashboard.util.DashboardPeriodo;
import com.main.tancol.dashboard.util.NativeQueryBuilder;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Component
public class DashboardConsumoQuery {

    @PersistenceContext
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
    public List<ConsumoClientesPorDiaResponseDTO> consumoPorDia(DashboardKpiFilter filter, DashboardPeriodo periodo) {
        NativeQueryBuilder sql = new NativeQueryBuilder()
                .append("""
                        SELECT CAST(l.fecha AS date) AS fecha,
                               COALESCE(SUM(lp.cantidad), 0) AS unidades,
                               COUNT(DISTINCT lp.cliente_id) AS clientes_atendidos,
                               COALESCE(SUM(lp.importe), 0) AS importe
                        FROM liquidacion_partidas lp
                                 INNER JOIN liquidacion l ON lp.liquidacion_origen_id_liquidacion = l.id_liquidacion
                        WHERE lp.cliente_id IS NOT NULL
                        """);
        applyPeriodo(sql, periodo);
        applyConsumoFilters(sql, filter);
        sql.append(" GROUP BY CAST(l.fecha AS date) ORDER BY fecha");

        List<Object[]> rows = sql.create(entityManager).getResultList();
        return rows.stream().map(row -> ConsumoClientesPorDiaResponseDTO.builder()
                .fecha(DashboardNativeTypes.toLocalDate(row[0]))
                .unidades(DashboardNativeTypes.toBigDecimal(row[1]))
                .clientesAtendidos(DashboardNativeTypes.toInt(row[2]))
                .importe(DashboardNativeTypes.toBigDecimal(row[3]))
                .build()
        ).toList();
    }

    @SuppressWarnings("unchecked")
    public List<TopClientePgExpResponseDTO> topClientesPgExp(DashboardKpiFilter filter,
                                                             DashboardPeriodo periodo,
                                                             DashboardKpiProperties properties) {
        List<UUID> conceptosPg = hasConceptos(properties.getPg())
                ? properties.getPg()
                : List.of(UUID.fromString("00000000-0000-0000-0000-000000000000"));
        List<UUID> conceptosExp = hasConceptos(properties.getExp())
                ? properties.getExp()
                : List.of(UUID.fromString("00000000-0000-0000-0000-000000000000"));
        List<UUID> conceptosOficiales = properties.forzarConceptosOficiales(filter.getConceptosVenta());
        if (conceptosOficiales.isEmpty()) {
            return List.of();
        }

        NativeQueryBuilder sql = new NativeQueryBuilder()
                .append("""
                        SELECT c.id,
                               c.nombre_comercial,
                               COALESCE(SUM(lp.cantidad), 0) AS unidades_totales,
                               COALESCE(SUM(CASE WHEN lp.conceptos_ventas_id IN (:conceptosPg) THEN lp.cantidad ELSE 0 END), 0) AS unidades_pg,
                               COALESCE(SUM(CASE WHEN lp.conceptos_ventas_id IN (:conceptosExp) THEN lp.cantidad ELSE 0 END), 0) AS unidades_exp,
                               COALESCE(SUM(lp.importe), 0) AS importe_total,
                               MAX(CAST(l.fecha AS date)) AS ultima_fecha
                        FROM liquidacion_partidas lp
                                 INNER JOIN liquidacion l ON lp.liquidacion_origen_id_liquidacion = l.id_liquidacion
                                 INNER JOIN clientes c ON c.id = lp.cliente_id
                        WHERE lp.cliente_id IS NOT NULL
                          AND lp.conceptos_ventas_id IN (:conceptosOficiales)
                        """)
                .param("conceptosPg", conceptosPg)
                .param("conceptosExp", conceptosExp)
                .param("conceptosOficiales", conceptosOficiales);
        applyPeriodo(sql, periodo);
        applyConsumoFilters(sql, filter);
        applyClienteEstado(sql, filter, "c");
        sql.append("""
                 GROUP BY c.id, c.nombre_comercial
                 ORDER BY unidades_totales DESC
                """);

        Query query = sql.create(entityManager);
        query.setMaxResults(filter.getLimit());
        List<Object[]> rows = query.getResultList();
        return rows.stream().map(row -> TopClientePgExpResponseDTO.builder()
                .idCliente(DashboardNativeTypes.toUuid(row[0]))
                .cliente(DashboardNativeTypes.toStringValue(row[1]))
                .unidadesTotales(DashboardNativeTypes.toBigDecimal(row[2]))
                .unidadesPg(DashboardNativeTypes.toBigDecimal(row[3]))
                .unidadesExp(DashboardNativeTypes.toBigDecimal(row[4]))
                .importeTotal(DashboardNativeTypes.toBigDecimal(row[5]))
                .ultimaFechaConsumo(DashboardNativeTypes.toLocalDate(row[6]))
                .build()
        ).toList();
    }

    @SuppressWarnings("unchecked")
    public List<ClienteSinConsumoResponseDTO> clientesSinConsumo(DashboardKpiFilter filter, LocalDate fechaCorte) {
        NativeQueryBuilder sql = new NativeQueryBuilder()
                .append("""
                        SELECT c.id,
                               c.nombre_comercial,
                               ult.ultima_fecha,
                               CAST(:fechaCorte AS date)
                                   - COALESCE(ult.ultima_fecha, CAST(COALESCE(c.fecha_alta, c.created_at) AS date)) AS dias_sin_consumir,
                               COALESCE(eq.equipos_asignados, 0) AS equipos_asignados,
                               CASE WHEN ult.ultima_fecha IS NULL THEN TRUE ELSE FALSE END AS nunca_ha_consumido
                        FROM clientes c
                                 LEFT JOIN (
                            SELECT lp.cliente_id,
                                   MAX(CAST(l.fecha AS date)) AS ultima_fecha
                            FROM liquidacion_partidas lp
                                     INNER JOIN liquidacion l ON lp.liquidacion_origen_id_liquidacion = l.id_liquidacion
                            WHERE lp.cliente_id IS NOT NULL
                        """);
        applyCancelacionFilters(sql, filter);
        sql.append("""
                            GROUP BY lp.cliente_id
                        ) ult ON ult.cliente_id = c.id
                                 LEFT JOIN (
                            SELECT ea.cliente_id,
                                   COUNT(*) AS equipos_asignados
                            FROM equipo_asignaciones ea
                            WHERE COALESCE(ea.deleted, FALSE) = FALSE
                              AND ea.activa = TRUE
                            GROUP BY ea.cliente_id
                        ) eq ON eq.cliente_id = c.id
                        WHERE COALESCE(c.deleted, FALSE) = FALSE
                        """)
                .param("fechaCorte", fechaCorte);
        applyClienteEstado(sql, filter, "c");
        if (DashboardNativeTypes.hasValues(filter.getIdClientes())) {
            sql.append(" AND c.id IN (:idClientes)").param("idClientes", filter.getIdClientes());
        }
        if (DashboardNativeTypes.hasValues(filter.getIdRutas())) {
            sql.append(" AND c.ruta_asociada_id IN (:idRutasCliente)").param("idRutasCliente", filter.getIdRutas());
        }
        if (!filter.isIncluirNuncaConsumieron()) {
            sql.append(" AND ult.ultima_fecha IS NOT NULL");
        }
        sql.append("""
                 ORDER BY CASE WHEN ult.ultima_fecha IS NULL THEN 1 ELSE 0 END DESC,
                          dias_sin_consumir DESC NULLS LAST,
                          c.nombre_comercial
                """);

        Query query = sql.create(entityManager);
        query.setMaxResults(filter.getLimit());
        List<Object[]> rows = query.getResultList();
        return rows.stream().map(row -> ClienteSinConsumoResponseDTO.builder()
                .idCliente(DashboardNativeTypes.toUuid(row[0]))
                .cliente(DashboardNativeTypes.toStringValue(row[1]))
                .ultimaFechaConsumo(DashboardNativeTypes.toLocalDate(row[2]))
                .diasSinConsumir(DashboardNativeTypes.toIntegerOrNull(row[3]))
                .equiposAsignados(DashboardNativeTypes.toInt(row[4]))
                .nuncaHaConsumido(DashboardNativeTypes.toBoolean(row[5]))
                .build()
        ).toList();
    }

    private boolean hasConceptos(List<UUID> conceptos) {
        return DashboardNativeTypes.hasValues(conceptos);
    }

    private void applyPeriodo(NativeQueryBuilder sql, DashboardPeriodo periodo) {
        sql.append(" AND l.fecha >= :fechaInicio AND l.fecha < :fechaFinExclusivo")
                .param("fechaInicio", periodo.inicio())
                .param("fechaFinExclusivo", periodo.finExclusivo());
    }

    private void applyConsumoFilters(NativeQueryBuilder sql, DashboardKpiFilter filter) {
        applyCancelacionFilters(sql, filter);
        if (DashboardNativeTypes.hasValues(filter.getIdRutas())) {
            sql.append(" AND l.ruta_id IN (:idRutas)").param("idRutas", filter.getIdRutas());
        }
        if (DashboardNativeTypes.hasValues(filter.getIdClientes())) {
            sql.append(" AND lp.cliente_id IN (:idClientes)").param("idClientes", filter.getIdClientes());
        }
    }

    private void applyCancelacionFilters(NativeQueryBuilder sql, DashboardKpiFilter filter) {
        sql.append(" AND COALESCE(l.deleted, FALSE) = FALSE");
        if (!filter.isIncluirCancelados()) {
            sql.append(" AND l.estados_liquidacion = 'FINALIZADA'");
            sql.append(" AND COALESCE(lp.cancelada, FALSE) = FALSE");
        }
    }

    private void applyClienteEstado(NativeQueryBuilder sql, DashboardKpiFilter filter, String alias) {
        if (!filter.isIncluirClientesInactivos()) {
            sql.append(" AND COALESCE(").append(alias).append(".enabled, TRUE) = TRUE");
        }
    }
}
