package com.main.tancol.reportes.service;

import com.main.tancol.almacen.entities.OrdenInternaEntity;
import com.main.tancol.almacen.entities.partidas.PartidaOrdenInternaEntity;
import com.main.tancol.almacen.enums.EstadoOrdenInterna;
import com.main.tancol.almacen.repositories.PartidaOrdenInternaRepository;
import com.main.tancol.empleados.entities.EmpleadosEntity;
import com.main.tancol.producto.entities.insumos.CategoriaProductoEntity;
import com.main.tancol.producto.entities.insumos.ProductoBasicoEntity;
import com.main.tancol.producto.service.insumo.CategoriaService;
import com.main.tancol.reportes.dto.request.ReporteConsumoAreaProductoRequestDTO;
import com.main.tancol.reportes.dto.response.ReporteConsumoAreaProductoResponseDTO;
import com.main.tancol.reportes.dto.response.ReporteConsumoAreaProductoResponseDTO.Fila;
import com.main.tancol.reportes.dto.response.ReporteConsumoAreaProductoResponseDTO.Grupo;
import com.main.tancol.reportes.dto.response.ReporteConsumoAreaProductoResponseDTO.TotalEmpleado;
import com.main.tancol.reportes.dto.response.ReporteConsumoAreaProductoResponseDTO.TotalInsumo;
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
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ConsumoAreaProductoReporteService {

    private static final String SIN_USO = "Sin uso";
    private static final String SIN_INSUMO = "Sin insumo";
    private static final String SIN_EMPLEADO = "Sin empleado";

    private final PartidaOrdenInternaRepository partidaOrdenInternaRepository;
    private final CategoriaService categoriaService;

    @Transactional(readOnly = true)
    public ReporteConsumoAreaProductoResponseDTO generar(ReporteConsumoAreaProductoRequestDTO filtro) {
        ReporteConsumoAreaProductoRequestDTO criterios = filtro == null
                ? new ReporteConsumoAreaProductoRequestDTO()
                : filtro;

        Set<UUID> categorias = categoriaService.expandirConSubcategorias(criterios.getIdCategorias());
        List<PartidaOrdenInternaEntity> partidas = partidaOrdenInternaRepository.findAll(especificacion(criterios, categorias));

        Map<String, GrupoAcumulado> grupos = new LinkedHashMap<>();
        Map<String, TotalAcumulado> porInsumo = new LinkedHashMap<>();
        Map<String, TotalAcumulado> porEmpleado = new LinkedHashMap<>();

        for (PartidaOrdenInternaEntity partida : partidas) {
            OrdenInternaEntity orden = partida.getOrdenInternaEntity();
            ProductoBasicoEntity producto = partida.getProductoBasicoEntity();
            EmpleadosEntity empleado = orden == null ? null : orden.getEmpleadoRecibe();

            String area = area(orden);
            String insumo = insumo(producto);
            String nombreEmpleado = nombreEmpleado(empleado);
            BigDecimal cantidad = partida.getCantidad() == null ? BigDecimal.ZERO : partida.getCantidad();
            String claveInsumo = producto == null || producto.getId() == null ? SIN_INSUMO : producto.getId().toString();
            String claveEmpleado = empleado == null || empleado.getId() == null ? SIN_EMPLEADO : empleado.getId().toString();

            Fila fila = Fila.builder()
                    .fecha(orden == null ? null : orden.getFechaElaboracion())
                    .empleado(nombreEmpleado)
                    .insumo(insumo)
                    .cantidadSolicitada(partida.getCantidad())
                    .comentarioPartida(partida.getComentario())
                    .build();

            grupos.computeIfAbsent(area + "\0" + claveInsumo, key -> new GrupoAcumulado(area, insumo))
                    .agregar(fila, cantidad);
            porInsumo.computeIfAbsent(claveInsumo, key -> new TotalAcumulado(insumo)).sumar(cantidad);
            porEmpleado.computeIfAbsent(claveEmpleado, key -> new TotalAcumulado(nombreEmpleado)).sumar(cantidad);
        }

        List<Grupo> gruposOrdenados = grupos.values().stream()
                .map(GrupoAcumulado::toDto)
                .sorted(Comparator.comparing(Grupo::getArea, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(Grupo::getInsumo, String.CASE_INSENSITIVE_ORDER))
                .toList();

        return ReporteConsumoAreaProductoResponseDTO.builder()
                .grupos(gruposOrdenados)
                .totalesPorInsumo(porInsumo.values().stream()
                        .map(total -> TotalInsumo.builder().insumo(total.etiqueta).cantidad(total.cantidad).build())
                        .sorted(Comparator.comparing(TotalInsumo::getInsumo, String.CASE_INSENSITIVE_ORDER))
                        .toList())
                .totalesPorEmpleado(porEmpleado.values().stream()
                        .map(total -> TotalEmpleado.builder().empleado(total.etiqueta).cantidad(total.cantidad).build())
                        .sorted(Comparator.comparing(TotalEmpleado::getEmpleado, String.CASE_INSENSITIVE_ORDER))
                        .toList())
                .build();
    }

    private Specification<PartidaOrdenInternaEntity> especificacion(ReporteConsumoAreaProductoRequestDTO filtro, Set<UUID> categorias) {
        return (root, query, cb) -> {
            Join<PartidaOrdenInternaEntity, OrdenInternaEntity> orden = root.join("ordenInternaEntity", JoinType.INNER);
            Join<OrdenInternaEntity, EmpleadosEntity> empleado = orden.join("empleadoRecibe", JoinType.LEFT);
            Join<PartidaOrdenInternaEntity, ProductoBasicoEntity> producto = root.join("productoBasicoEntity", JoinType.LEFT);
            Join<ProductoBasicoEntity, CategoriaProductoEntity> categoria = producto.join("categoriaProductoEntity", JoinType.LEFT);

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
            if (tieneValores(filtro.getIdEmpleados())) {
                predicados.add(empleado.get("id").in(filtro.getIdEmpleados()));
            }
            if (tieneValores(filtro.getIdInsumos())) {
                predicados.add(producto.get("id").in(filtro.getIdInsumos()));
            }
            if (!categorias.isEmpty()) {
                predicados.add(categoria.get("id").in(categorias));
            }
            if (tieneValores(filtro.getAlias())) {
                predicados.add(producto.get("alias").in(filtro.getAlias()));
            }

            if (query != null) {
                query.distinct(true);
            }
            return cb.and(predicados.toArray(Predicate[]::new));
        };
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

    private static String insumo(ProductoBasicoEntity producto) {
        if (producto == null || !StringUtils.hasText(producto.getNombre())) {
            return SIN_INSUMO;
        }
        return producto.getNombre().trim();
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

    private static final class GrupoAcumulado {
        private final String area;
        private final String insumo;
        private BigDecimal cantidad = BigDecimal.ZERO;
        private final List<Fila> filas = new ArrayList<>();

        private GrupoAcumulado(String area, String insumo) {
            this.area = area;
            this.insumo = insumo;
        }

        private void agregar(Fila fila, BigDecimal cantidadFila) {
            filas.add(fila);
            cantidad = cantidad.add(cantidadFila);
        }

        private Grupo toDto() {
            filas.sort(Comparator.comparing(Fila::getFecha, Comparator.nullsLast(Comparator.naturalOrder())));
            return Grupo.builder()
                    .area(area)
                    .insumo(insumo)
                    .cantidad(cantidad)
                    .filas(filas)
                    .build();
        }
    }

    private static final class TotalAcumulado {
        private final String etiqueta;
        private BigDecimal cantidad = BigDecimal.ZERO;

        private TotalAcumulado(String etiqueta) {
            this.etiqueta = etiqueta;
        }

        private void sumar(BigDecimal valor) {
            cantidad = cantidad.add(valor);
        }
    }
}
