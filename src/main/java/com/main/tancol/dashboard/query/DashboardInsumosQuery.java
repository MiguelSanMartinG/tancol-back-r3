package com.main.tancol.dashboard.query;

import com.main.tancol.almacen.entities.OrdenInternaEntity;
import com.main.tancol.almacen.entities.UnidadesMedidaEntity;
import com.main.tancol.almacen.entities.partidas.PartidaOrdenInternaEntity;
import com.main.tancol.almacen.enums.EstadoOrdenInterna;
import com.main.tancol.almacen.repositories.PartidaOrdenInternaRepository;
import com.main.tancol.dashboard.dto.response.TopDepartamentoInsumosResponseDTO;
import com.main.tancol.dashboard.dto.response.TopDepartamentoInsumosResponseDTO.Producto;
import com.main.tancol.dashboard.dto.response.TopInsumoCompradoResponseDTO;
import com.main.tancol.dashboard.util.DashboardPeriodo;
import com.main.tancol.orden.entities.OrdenEntity;
import com.main.tancol.orden.entities.PartidaOrdenEntity;
import com.main.tancol.orden.enums.SolicitudEstatus;
import com.main.tancol.orden.repositories.PartidaOrdenRepository;
import com.main.tancol.producto.entities.insumos.ProductoBasicoEntity;
import jakarta.persistence.criteria.Fetch;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DashboardInsumosQuery {

    static final int TOP_PRODUCTOS = 3;
    private static final String SIN_USO = "Sin uso";
    private static final String SIN_INSUMO = "Sin insumo";

    private final PartidaOrdenInternaRepository partidaOrdenInternaRepository;
    private final PartidaOrdenRepository partidaOrdenRepository;

    @Transactional(readOnly = true)
    public List<TopDepartamentoInsumosResponseDTO> topDepartamentos(DashboardPeriodo periodo, int limit) {
        List<PartidaOrdenInternaEntity> partidas = partidaOrdenInternaRepository.findAll(especificacion(periodo));

        Map<String, DepartamentoAcumulado> departamentos = new LinkedHashMap<>();
        for (PartidaOrdenInternaEntity partida : partidas) {
            String departamento = departamento(partida.getOrdenInternaEntity());
            BigDecimal cantidad = partida.getCantidad() == null ? BigDecimal.ZERO : partida.getCantidad();
            departamentos.computeIfAbsent(clave(departamento), key -> new DepartamentoAcumulado(departamento))
                    .agregar(partida, cantidad);
        }

        return departamentos.values().stream()
                .sorted(Comparator.comparing(DepartamentoAcumulado::getCantidadTotal).reversed()
                        .thenComparing(DepartamentoAcumulado::getNombre, String.CASE_INSENSITIVE_ORDER))
                .limit(Math.max(limit, 1))
                .map(DepartamentoAcumulado::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TopInsumoCompradoResponseDTO> topInsumosComprados(DashboardPeriodo periodo, int limit) {
        List<PartidaOrdenEntity> partidas = partidaOrdenRepository.findAll(especificacionCompra(periodo));

        Map<String, InsumoCompradoAcumulado> insumos = new LinkedHashMap<>();
        for (PartidaOrdenEntity partida : partidas) {
            ProductoBasicoEntity producto = partida.getProducto();
            String nombre = producto(producto);
            String unidad = unidadCompra(partida);
            BigDecimal cantidad = partida.getCantidad() == null ? BigDecimal.ZERO : BigDecimal.valueOf(partida.getCantidad());
            String idProducto = producto == null || producto.getId() == null ? nombre : producto.getId().toString();
            String claveProducto = idProducto + "\0" + (unidad == null ? "" : clave(unidad));
            insumos.computeIfAbsent(claveProducto,
                            key -> new InsumoCompradoAcumulado(producto == null ? null : producto.getId(), nombre, unidad))
                    .sumar(cantidad);
        }

        return insumos.values().stream()
                .sorted(Comparator.comparing(InsumoCompradoAcumulado::getCantidad).reversed()
                        .thenComparing(InsumoCompradoAcumulado::getNombre, String.CASE_INSENSITIVE_ORDER))
                .limit(Math.max(limit, 1))
                .map(InsumoCompradoAcumulado::toDto)
                .toList();
    }

    private Specification<PartidaOrdenInternaEntity> especificacion(DashboardPeriodo periodo) {
        return (root, query, cb) -> {
            Fetch<PartidaOrdenInternaEntity, OrdenInternaEntity> ordenFetch =
                    root.fetch("ordenInternaEntity", JoinType.INNER);
            @SuppressWarnings("unchecked")
            Join<PartidaOrdenInternaEntity, OrdenInternaEntity> orden =
                    (Join<PartidaOrdenInternaEntity, OrdenInternaEntity>) ordenFetch;
            root.fetch("productoBasicoEntity", JoinType.LEFT);
            root.fetch("unidadesMedidaEntity", JoinType.LEFT);

            List<Predicate> predicados = new ArrayList<>();
            predicados.add(cb.isFalse(orden.get("deleted")));
            predicados.add(cb.or(
                    cb.isNull(orden.get("estadoOrdenInterna")),
                    cb.notEqual(orden.get("estadoOrdenInterna"), EstadoOrdenInterna.BORRADOR)
            ));
            predicados.add(cb.greaterThanOrEqualTo(orden.get("fechaElaboracion"), periodo.inicio()));
            predicados.add(cb.lessThan(orden.get("fechaElaboracion"), periodo.finExclusivo()));
            if (query != null) {
                query.distinct(true);
            }
            return cb.and(predicados.toArray(Predicate[]::new));
        };
    }

    private Specification<PartidaOrdenEntity> especificacionCompra(DashboardPeriodo periodo) {
        return (root, query, cb) -> {
            Fetch<PartidaOrdenEntity, OrdenEntity> ordenFetch = root.fetch("solicitudCompraEntity", JoinType.INNER);
            @SuppressWarnings("unchecked")
            Join<PartidaOrdenEntity, OrdenEntity> orden = (Join<PartidaOrdenEntity, OrdenEntity>) ordenFetch;
            root.fetch("producto", JoinType.LEFT);
            root.fetch("unidadesMedida", JoinType.LEFT);

            List<Predicate> predicados = new ArrayList<>();
            predicados.add(cb.isFalse(orden.get("deleted")));
            predicados.add(cb.or(
                    cb.isNull(orden.get("solicitudEstatus")),
                    orden.get("solicitudEstatus").in(SolicitudEstatus.BORRADOR, SolicitudEstatus.CANCELADA).not()
            ));
            predicados.add(cb.greaterThanOrEqualTo(orden.get("getFechaCreacionSolicitud"), periodo.inicio()));
            predicados.add(cb.lessThan(orden.get("getFechaCreacionSolicitud"), periodo.finExclusivo()));
            if (query != null) {
                query.distinct(true);
            }
            return cb.and(predicados.toArray(Predicate[]::new));
        };
    }

    private static String departamento(OrdenInternaEntity orden) {
        if (orden == null || !StringUtils.hasText(orden.getUso())) {
            return SIN_USO;
        }
        return orden.getUso().trim();
    }

    private static String clave(String valor) {
        return valor.toLowerCase(Locale.ROOT);
    }

    private static String producto(ProductoBasicoEntity producto) {
        if (producto == null) {
            return SIN_INSUMO;
        }
        if (StringUtils.hasText(producto.getNombre())) {
            return producto.getNombre().trim();
        }
        if (StringUtils.hasText(producto.getAlias())) {
            return producto.getAlias().trim();
        }
        return SIN_INSUMO;
    }

    private static String unidad(PartidaOrdenInternaEntity partida) {
        UnidadesMedidaEntity unidad = partida.getUnidadesMedidaEntity();
        if (unidad == null && partida.getProductoBasicoEntity() != null) {
            unidad = partida.getProductoBasicoEntity().getUnidadMedidaDefault();
        }
        if (unidad == null) {
            return null;
        }
        if (StringUtils.hasText(unidad.getAlias())) {
            return unidad.getAlias().trim();
        }
        if (StringUtils.hasText(unidad.getNombre())) {
            return unidad.getNombre().trim();
        }
        return null;
    }

    private static String unidadCompra(PartidaOrdenEntity partida) {
        UnidadesMedidaEntity unidad = partida.getUnidadesMedida();
        if (unidad == null && partida.getProducto() != null) {
            unidad = partida.getProducto().getUnidadMedidaDefault();
        }
        return etiquetaUnidad(unidad);
    }

    private static String etiquetaUnidad(UnidadesMedidaEntity unidad) {
        if (unidad == null) {
            return null;
        }
        if (StringUtils.hasText(unidad.getAlias())) {
            return unidad.getAlias().trim();
        }
        if (StringUtils.hasText(unidad.getNombre())) {
            return unidad.getNombre().trim();
        }
        return null;
    }

    private static final class DepartamentoAcumulado {
        private final String nombre;
        private BigDecimal cantidadTotal = BigDecimal.ZERO;
        private final Map<String, ProductoAcumulado> productos = new LinkedHashMap<>();

        private DepartamentoAcumulado(String nombre) {
            this.nombre = nombre;
        }

        private String getNombre() {
            return nombre;
        }

        private BigDecimal getCantidadTotal() {
            return cantidadTotal;
        }

        private void agregar(PartidaOrdenInternaEntity partida, BigDecimal cantidad) {
            cantidadTotal = cantidadTotal.add(cantidad);
            ProductoBasicoEntity insumo = partida.getProductoBasicoEntity();
            String nombreProducto = producto(insumo);
            String unidadProducto = unidad(partida);
            String idProducto = insumo == null || insumo.getId() == null ? nombreProducto : insumo.getId().toString();
            String claveProducto = idProducto + "\0" + (unidadProducto == null ? "" : clave(unidadProducto));
            productos.computeIfAbsent(claveProducto,
                            key -> new ProductoAcumulado(insumo == null ? null : insumo.getId(), nombreProducto, unidadProducto))
                    .sumar(cantidad);
        }

        private TopDepartamentoInsumosResponseDTO toDto() {
            List<Producto> top = productos.values().stream()
                    .sorted(Comparator.comparing(ProductoAcumulado::getCantidad).reversed()
                            .thenComparing(ProductoAcumulado::getNombre, String.CASE_INSENSITIVE_ORDER))
                    .limit(TOP_PRODUCTOS)
                    .map(ProductoAcumulado::toDto)
                    .toList();
            return TopDepartamentoInsumosResponseDTO.builder()
                    .departamento(nombre)
                    .cantidadTotal(cantidadTotal)
                    .productos(top)
                    .build();
        }
    }

    private static final class ProductoAcumulado {
        private final UUID idProducto;
        private final String nombre;
        private final String unidad;
        private BigDecimal cantidad = BigDecimal.ZERO;

        private ProductoAcumulado(UUID idProducto, String nombre, String unidad) {
            this.idProducto = idProducto;
            this.nombre = nombre;
            this.unidad = unidad;
        }

        private String getNombre() {
            return nombre;
        }

        private BigDecimal getCantidad() {
            return cantidad;
        }

        private void sumar(BigDecimal valor) {
            cantidad = cantidad.add(valor);
        }

        private Producto toDto() {
            return Producto.builder()
                    .idProducto(idProducto)
                    .producto(nombre)
                    .cantidad(cantidad)
                    .unidad(unidad)
                    .build();
        }
    }

    private static final class InsumoCompradoAcumulado {
        private final UUID idProducto;
        private final String nombre;
        private final String unidad;
        private BigDecimal cantidad = BigDecimal.ZERO;

        private InsumoCompradoAcumulado(UUID idProducto, String nombre, String unidad) {
            this.idProducto = idProducto;
            this.nombre = nombre;
            this.unidad = unidad;
        }

        private String getNombre() {
            return nombre;
        }

        private BigDecimal getCantidad() {
            return cantidad;
        }

        private void sumar(BigDecimal valor) {
            cantidad = cantidad.add(valor);
        }

        private TopInsumoCompradoResponseDTO toDto() {
            return TopInsumoCompradoResponseDTO.builder()
                    .idProducto(idProducto)
                    .producto(nombre)
                    .cantidad(cantidad)
                    .unidad(unidad)
                    .build();
        }
    }
}
