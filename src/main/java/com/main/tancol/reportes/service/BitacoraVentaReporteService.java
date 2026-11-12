package com.main.tancol.reportes.service;

import com.main.tancol.logistica.enums.TipoProceso;
import com.main.tancol.reportes.dto.request.ReporteBitacorasVentasRequestDTO;
import com.main.tancol.reportes.dto.response.ReporteBitacorasVentasResponseDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BitacoraVentaReporteService {

    @PersistenceContext
    private final EntityManager entityManager;

    public List<ReporteBitacorasVentasResponseDTO> getBitacorasVentas(ReporteBitacorasVentasRequestDTO filtro) {
        Query query = entityManager.createNativeQuery(
                "SELECT * FROM get_bitacoras_ventas(:fechaInicio,:fechaFin,:folio,:rutas,:procesos,:choferes,:ayudantes,:productos,:vehiculos,:fechaFirma,:rotos,:fueraEspecificacion,:rotoPorMaquina)");

        query.setParameter("fechaInicio", filtro.getFechaInicio());
        query.setParameter("fechaFin", filtro.getFechaFin());
        query.setParameter("folio", blankToNull(filtro.getFolio()));
        query.setParameter("rutas", toUuidArray(filtro.getIdRutas()));
        query.setParameter("procesos", toProcesoArray(filtro.getProcesos()));
        query.setParameter("choferes", toUuidArray(filtro.getIdChoferes()));
        query.setParameter("ayudantes", toUuidArray(filtro.getIdAyudantes()));
        query.setParameter("productos", toUuidArray(filtro.getIdProductos()));
        query.setParameter("vehiculos", toUuidArray(filtro.getIdVehiculos()));
        query.setParameter("fechaFirma", filtro.getFechaFirma());
        query.setParameter("rotos", filtro.getRotos());
        query.setParameter("fueraEspecificacion", filtro.getFueraEspecificacion());
        query.setParameter("rotoPorMaquina", filtro.getRotoPorMaquina());

        @SuppressWarnings("unchecked")
        List<Object[]> resultado = query.getResultList();

        return resultado.stream().map(row -> ReporteBitacorasVentasResponseDTO.builder()
                .idBitacora(toUuid(row[0]))
                .idTrayecto(toUuid(row[1]))
                .idVenta(toUuid(row[2]))
                .folio((String) row[3])
                .fecha(toLocalDate(row[4]))
                .estatusBitacora((String) row[5])
                .nombreRuta((String) row[6])
                .tipoProceso((String) row[7])
                .chofer((String) row[8])
                .ayudantes(toStringValue(row[9]))
                .nombreProducto((String) row[10])
                .vehiculo((String) row[11])
                .fechaFirma(toLocalDateTime(row[12]))
                .cantidadSalida(toBigDecimal(row[13]))
                .cantidadLlegada(toBigDecimal(row[14]))
                .cantidadVendida(toBigDecimal(row[15]))
                .huecos(toBigDecimal(row[16]))
                .rotos(toBigDecimal(row[17]))
                .fueraEspecificacion(toStringValue(row[18]))
                .cantidadFueraEspecificacion(toBigDecimal(row[19]))
                .estadoVentaRuta((String) row[20])
                .mandadoALiquidacion((Boolean) row[21])
                .comentario((String) row[22])
                .build()).toList();
    }

    private UUID[] toUuidArray(List<UUID> values) {
        return values != null && !values.isEmpty() ? values.toArray(UUID[]::new) : null;
    }

    private String[] toProcesoArray(List<TipoProceso> procesos) {
        return procesos != null && !procesos.isEmpty()
                ? procesos.stream().map(TipoProceso::name).toArray(String[]::new)
                : null;
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private UUID toUuid(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof UUID uuid) {
            return uuid;
        }
        return UUID.fromString(value.toString());
    }

    private String toStringValue(Object value) {
        return value == null ? null : value.toString();
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        if (value instanceof BigDecimal bd) {
            return bd;
        }
        if (value instanceof Number n) {
            return new BigDecimal(n.toString());
        }
        if (value instanceof String s && !s.isBlank()) {
            return new BigDecimal(s.trim());
        }
        return BigDecimal.ZERO;
    }

    private LocalDate toLocalDate(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof LocalDate localDate) {
            return localDate;
        }
        if (value instanceof Date date) {
            return date.toLocalDate();
        }
        if (value instanceof Timestamp timestamp) {
            return timestamp.toLocalDateTime().toLocalDate();
        }
        if (value instanceof java.util.Date date) {
            return new Date(date.getTime()).toLocalDate();
        }
        return LocalDate.parse(value.toString());
    }

    private LocalDateTime toLocalDateTime(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof LocalDateTime localDateTime) {
            return localDateTime;
        }
        if (value instanceof Timestamp timestamp) {
            return timestamp.toLocalDateTime();
        }
        if (value instanceof Date date) {
            return date.toLocalDate().atStartOfDay();
        }
        return LocalDateTime.parse(value.toString());
    }
}
