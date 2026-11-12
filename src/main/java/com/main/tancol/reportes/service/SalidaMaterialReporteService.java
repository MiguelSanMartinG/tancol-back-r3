package com.main.tancol.reportes.service;

import com.main.tancol.almacen.entities.AlmacenEntity;
import com.main.tancol.almacen.entities.OrdenInternaEntity;
import com.main.tancol.almacen.entities.UnidadesMedidaEntity;
import com.main.tancol.almacen.entities.partidas.PartidaOrdenInternaEntity;
import com.main.tancol.almacen.enums.EstadoOrdenInterna;
import com.main.tancol.almacen.repositories.PartidaOrdenInternaRepository;
import com.main.tancol.empleados.entities.EmpleadosEntity;
import com.main.tancol.producto.entities.insumos.ProductoBasicoEntity;
import com.main.tancol.reportes.dto.request.ReporteSalidaMaterialRequestDTO;
import com.main.tancol.reportes.dto.response.ReporteSalidaMaterialResponseDTO;
import com.main.tancol.reportes.dto.response.ReporteSalidaMaterialResponseDTO.Fila;
import com.main.tancol.reportes.dto.response.ReporteSalidaMaterialResponseDTO.TotalInsumo;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SalidaMaterialReporteService {

    private static final String SIN_USO = "Sin uso";
    private static final String SIN_INSUMO = "Sin insumo";
    private static final String SIN_EMPLEADO = "Sin empleado";
    private static final String SIN_ALMACEN = "Sin almacén";
    private static final String SIN_UNIDAD = "Sin unidad";

    private final PartidaOrdenInternaRepository partidaOrdenInternaRepository;

    @Transactional(readOnly = true)
    public ReporteSalidaMaterialResponseDTO generar(ReporteSalidaMaterialRequestDTO filtro) {
        ReporteSalidaMaterialRequestDTO criterios = filtro == null
                ? new ReporteSalidaMaterialRequestDTO()
                : filtro;

        List<Fila> filas = partidaOrdenInternaRepository.findAll(especificacion(criterios)).stream()
                .map(this::fila)
                .sorted(Comparator.comparing(Fila::getFecha, Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(Fila::getFolio, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER))
                        .thenComparing(Fila::getInsumo, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .toList();

        Map<String, TotalAcumulado> porInsumo = new LinkedHashMap<>();
        for (Fila fila : filas) {
            String insumo = fila.getInsumo();
            String unidad = fila.getUnidadMedida();
            porInsumo.computeIfAbsent(insumo + "\0" + unidad, key -> new TotalAcumulado(insumo, unidad))
                    .sumar(fila.getCantidad() == null ? BigDecimal.ZERO : fila.getCantidad());
        }

        List<TotalInsumo> totales = porInsumo.values().stream()
                .map(total -> TotalInsumo.builder()
                        .insumo(total.insumo)
                        .unidadMedida(total.unidadMedida)
                        .cantidad(total.cantidad)
                        .build())
                .sorted(Comparator.comparing(TotalInsumo::getInsumo, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(TotalInsumo::getUnidadMedida, String.CASE_INSENSITIVE_ORDER))
                .toList();

        return ReporteSalidaMaterialResponseDTO.builder()
                .filas(filas)
                .totalesPorInsumo(totales)
                .totalFilas(filas.size())
                .build();
    }

    private Fila fila(PartidaOrdenInternaEntity partida) {
        OrdenInternaEntity orden = partida.getOrdenInternaEntity();
        ProductoBasicoEntity producto = partida.getProductoBasicoEntity();
        AlmacenEntity almacen = partida.getAlmacenEntity();
        UnidadesMedidaEntity unidad = partida.getUnidadesMedidaEntity();

        return Fila.builder()
                .idPartida(partida.getIdPartida())
                .idOrdenInterna(orden == null ? null : orden.getIdOrdenInterna())
                .folio(orden == null ? null : orden.getFolio())
                .fecha(orden == null ? null : orden.getFechaElaboracion())
                .area(area(orden))
                .estado(orden == null || orden.getEstadoOrdenInterna() == null
                        ? null
                        : orden.getEstadoOrdenInterna().name())
                .empleadoEntrega(nombreEmpleado(orden == null ? null : orden.getEmpleadoEnvia()))
                .empleadoRecibe(nombreEmpleado(orden == null ? null : orden.getEmpleadoRecibe()))
                .idAlmacen(almacen == null ? null : almacen.getId())
                .almacen(texto(almacen == null ? null : almacen.getNombre(), SIN_ALMACEN))
                .idInsumo(producto == null ? null : producto.getId())
                .insumo(texto(producto == null ? null : producto.getNombre(), SIN_INSUMO))
                .alias(producto == null ? null : producto.getAlias())
                .unidadMedida(texto(unidad == null ? null : unidad.getNombre(), SIN_UNIDAD))
                .cantidad(partida.getCantidad())
                .comentarioPartida(partida.getComentario())
                .comentarioOrden(orden == null ? null : orden.getComentarios())
                .build();
    }

    private Specification<PartidaOrdenInternaEntity> especificacion(ReporteSalidaMaterialRequestDTO filtro) {
        return (root, query, cb) -> {
            Join<PartidaOrdenInternaEntity, OrdenInternaEntity> orden = root.join("ordenInternaEntity", JoinType.INNER);
            Join<OrdenInternaEntity, EmpleadosEntity> empleado = orden.join("empleadoRecibe", JoinType.LEFT);
            Join<PartidaOrdenInternaEntity, ProductoBasicoEntity> producto = root.join("productoBasicoEntity", JoinType.LEFT);
            Join<PartidaOrdenInternaEntity, AlmacenEntity> almacen = root.join("almacenEntity", JoinType.LEFT);

            List<Predicate> predicados = new ArrayList<>();
            predicados.add(cb.isFalse(orden.get("deleted")));
            predicados.add(cb.or(
                    cb.isNull(orden.get("estadoOrdenInterna")),
                    cb.notEqual(orden.get("estadoOrdenInterna"), EstadoOrdenInterna.BORRADOR)
            ));

            if (filtro.getFechaInicio() != null) {
                predicados.add(cb.greaterThanOrEqualTo(orden.get("fechaElaboracion"), filtro.getFechaInicio()));
            }
            if (filtro.getFechaFin() != null) {
                predicados.add(cb.lessThanOrEqualTo(orden.get("fechaElaboracion"), filtro.getFechaFin()));
            }
            predicadoAreas(filtro.getAreas(), orden, cb, predicados);
            if (tieneValores(filtro.getIdEmpleados())) {
                predicados.add(empleado.get("id").in(filtro.getIdEmpleados()));
            }
            if (tieneValores(filtro.getIdInsumos())) {
                predicados.add(producto.get("id").in(filtro.getIdInsumos()));
            }
            if (tieneValores(filtro.getIdAlmacenes())) {
                predicados.add(almacen.get("id").in(filtro.getIdAlmacenes()));
            }

            if (query != null) {
                query.distinct(true);
            }
            return cb.and(predicados.toArray(Predicate[]::new));
        };
    }

    private static void predicadoAreas(
            List<String> areas,
            Join<PartidaOrdenInternaEntity, OrdenInternaEntity> orden,
            CriteriaBuilder cb,
            List<Predicate> predicados) {
        List<String> normalizadas = normalizarAreas(areas);
        if (normalizadas.isEmpty()) {
            return;
        }

        List<Predicate> coincidencias = new ArrayList<>();
        coincidencias.add(cb.lower(cb.trim(orden.get("uso"))).in(normalizadas));
        if (normalizadas.contains(SIN_USO.toLowerCase(Locale.ROOT))) {
            coincidencias.add(cb.or(
                    cb.isNull(orden.get("uso")),
                    cb.equal(cb.trim(orden.get("uso")), "")
            ));
        }
        predicados.add(cb.or(coincidencias.toArray(Predicate[]::new)));
    }

    private static List<String> normalizarAreas(List<String> areas) {
        if (!tieneValores(areas)) {
            return List.of();
        }
        return areas.stream()
                .filter(StringUtils::hasText)
                .map(area -> area.trim().toLowerCase(Locale.ROOT))
                .distinct()
                .toList();
    }

    private static boolean tieneValores(Collection<?> valores) {
        return valores != null && !valores.isEmpty();
    }

    private static String area(OrdenInternaEntity orden) {
        if (orden == null || !StringUtils.hasText(orden.getUso())) {
            return SIN_USO;
        }
        return orden.getUso().trim();
    }

    private static String texto(String valor, String vacio) {
        return StringUtils.hasText(valor) ? valor.trim() : vacio;
    }

    private static String nombreEmpleado(EmpleadosEntity empleado) {
        if (empleado == null) {
            return SIN_EMPLEADO;
        }
        String nombre = Stream.of(empleado.getNombre(), empleado.getPrimerApellido(), empleado.getSegundoApellido())
                .filter(StringUtils::hasText)
                .map(String::trim)
                .collect(Collectors.joining(" "));
        return StringUtils.hasText(nombre) ? nombre : SIN_EMPLEADO;
    }

    private static final class TotalAcumulado {
        private final String insumo;
        private final String unidadMedida;
        private BigDecimal cantidad = BigDecimal.ZERO;

        private TotalAcumulado(String insumo, String unidadMedida) {
            this.insumo = insumo;
            this.unidadMedida = unidadMedida;
        }

        private void sumar(BigDecimal valor) {
            cantidad = cantidad.add(valor);
        }
    }
}
