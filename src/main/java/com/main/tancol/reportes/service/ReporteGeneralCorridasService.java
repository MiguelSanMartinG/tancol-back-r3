package com.main.tancol.reportes.service;

import com.main.tancol.empleados.entities.EmpleadosEntity;
import com.main.tancol.empleados.repositories.EmpleadosRepository;
import com.main.tancol.reportes.dto.response.ReporteGeneralCorridasFilaResponseDTO;
import com.main.tancol.reportes.dto.response.ReporteGeneralCorridasResponseDTO;
import jakarta.persistence.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReporteGeneralCorridasService {

    private static final String EMPLEADO_ID = "empleado_id";
    private static final String NOMBRE_EMPLEADO = "nombre_empleado";
    private static final Set<String> COLUMNAS_NO_NUMERICAS = Set.of(EMPLEADO_ID, NOMBRE_EMPLEADO);

    @PersistenceContext
    private final EntityManager entityManager;
    private final EmpleadosRepository empleadosRepository;

    public ReporteGeneralCorridasResponseDTO ejecutarRgFactura(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return ejecutar("scripts/rg_factura.sql", fechaInicio, fechaFin);
    }

    public ReporteGeneralCorridasResponseDTO ejecutarRgNofactura(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return ejecutar("scripts/rg_nofactura.sql", fechaInicio, fechaFin);
    }

    public ReporteGeneralCorridasResponseDTO ejecutarRgNofacturaAutoservicio(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return ejecutar("scripts/rg_nofactura_autoservicio.sql", fechaInicio, fechaFin);
    }

    public ReporteGeneralCorridasResponseDTO ejecutarRgCredito(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return ejecutar("scripts/rg_credito.sql", fechaInicio, fechaFin);
    }

    public ReporteGeneralCorridasResponseDTO ejecutarRpTdebito(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return ejecutar("scripts/rp_tdebito.sql", fechaInicio, fechaFin);
    }

    public ReporteGeneralCorridasResponseDTO ejecutarRpTcredito(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return ejecutar("scripts/rp_tcredito.sql", fechaInicio, fechaFin);
    }

    private ReporteGeneralCorridasResponseDTO ejecutar(String scriptClasspath, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        validarRango(fechaInicio, fechaFin);
        String sql = cargarSql(scriptClasspath);

        Query query = entityManager.createNativeQuery(sql, Tuple.class);
        query.setParameter("fechaInicio", fechaInicio);
        query.setParameter("fechaFin", fechaFin);

        @SuppressWarnings("unchecked")
        List<Tuple> tuples = query.getResultList();
        return construirRespuesta(tuples);
    }

    private void validarRango(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        if (fechaInicio == null || fechaFin == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "fechaInicio y fechaFin son obligatorias");
        }
        if (fechaFin.isBefore(fechaInicio)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "fechaFin no puede ser anterior a fechaInicio");
        }
    }

    private String cargarSql(String classpath) {
        try {
            ClassPathResource resource = new ClassPathResource(classpath);
            if (!resource.exists()) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Script no encontrado: " + classpath);
            }
            return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("Error al cargar script {}", classpath, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No se pudo leer el script del reporte");
        }
    }

    private ReporteGeneralCorridasResponseDTO construirRespuesta(List<Tuple> tuples) {
        if (tuples.isEmpty()) {
            return ReporteGeneralCorridasResponseDTO.builder()
                    .granTotal(BigDecimal.ZERO)
                    .build();
        }

        List<String> columnas = new ArrayList<>();
        for (TupleElement<?> element : tuples.get(0).getElements()) {
            String alias = element.getAlias().toLowerCase();
            if (!COLUMNAS_NO_NUMERICAS.contains(alias)) {
                columnas.add(alias);
            }
        }

        Map<String, BigDecimal> totalesPorColumna = new LinkedHashMap<>();
        columnas.forEach(c -> totalesPorColumna.put(c, BigDecimal.ZERO));

        List<ReporteGeneralCorridasFilaResponseDTO> filas = new ArrayList<>();
        for (Tuple tuple : tuples) {
            ReporteGeneralCorridasFilaResponseDTO fila = new ReporteGeneralCorridasFilaResponseDTO();
            fila.setEmpleadoId((UUID) tuple.get(EMPLEADO_ID));
            fila.setColumnas(new LinkedHashMap<>());
            BigDecimal totalFila = BigDecimal.ZERO;

            for (String columna : columnas) {
                BigDecimal importe = toBigDecimal(tuple.get(columna));
                fila.getColumnas().put(columna, importe);
                totalFila = totalFila.add(importe);
                totalesPorColumna.merge(columna, importe, BigDecimal::add);
            }

            fila.setTotalFila(totalFila);
            filas.add(fila);
        }

        enriquecerNombresEmpleados(filas);

        filas.sort(Comparator.comparing(
                ReporteGeneralCorridasFilaResponseDTO::getNombreEmpleado,
                Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)));

        BigDecimal granTotal = totalesPorColumna.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);

        return ReporteGeneralCorridasResponseDTO.builder()
                .columnas(columnas)
                .filas(filas)
                .totalesPorColumna(totalesPorColumna)
                .granTotal(granTotal)
                .build();
    }

    private void enriquecerNombresEmpleados(List<ReporteGeneralCorridasFilaResponseDTO> filas) {
        Set<UUID> ids = filas.stream()
                .map(ReporteGeneralCorridasFilaResponseDTO::getEmpleadoId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (ids.isEmpty()) {
            return;
        }

        Map<UUID, String> nombresPorId = StreamSupport.stream(empleadosRepository.findAllById(ids).spliterator(), false)
                .collect(Collectors.toMap(EmpleadosEntity::getId, this::nombreCompleto));

        for (ReporteGeneralCorridasFilaResponseDTO fila : filas) {
            if (StringUtils.hasText(fila.getNombreEmpleado())) {
                continue;
            }
            UUID id = fila.getEmpleadoId();
            fila.setNombreEmpleado(nombresPorId.getOrDefault(id, id != null ? id.toString() : "Sin empleado"));
        }
    }

    private String nombreCompleto(EmpleadosEntity empleado) {
        String nombre = Stream.of(empleado.getNombre(), empleado.getPrimerApellido(), empleado.getSegundoApellido())
                .filter(StringUtils::hasText)
                .map(String::trim)
                .collect(Collectors.joining(" "));
        return StringUtils.hasText(nombre) ? nombre : empleado.getId().toString();
    }

    private static BigDecimal toBigDecimal(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        if (value instanceof BigDecimal bd) {
            return bd;
        }
        if (value instanceof Number number) {
            return BigDecimal.valueOf(number.doubleValue());
        }
        return new BigDecimal(value.toString());
    }
}
