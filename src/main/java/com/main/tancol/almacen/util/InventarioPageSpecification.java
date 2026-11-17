package com.main.tancol.almacen.util;

import com.main.tancol.almacen.entities.AlmacenEntity;
import com.main.tancol.almacen.entities.InventarioEntity;
import com.main.tancol.almacen.entities.UnidadesMedidaEntity;
import com.main.tancol.common.exceptions.exeption.FilterNotFound;
import com.main.tancol.common.specification.PagePredicates;
import com.main.tancol.producto.SubcategoriaProductoBasico;
import com.main.tancol.producto.entities.insumos.CategoriaProductoEntity;
import com.main.tancol.producto.entities.insumos.ProductoBasicoEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Especificacion de GET /inventario/page (Inventario general e Inventario inicial).
 * <p>
 * Almacen, producto, categoria y unidad son ManyToOne: se resuelven con LEFT JOIN (fetch en la consulta
 * de datos, join simple en la de conteo), asi que no se duplican filas y el total es correcto.
 */
public class InventarioPageSpecification implements Specification<InventarioEntity> {

    private final String search;
    private final Map<String, String> filters;

    public InventarioPageSpecification(String search, Map<String, String> filters) {
        this.search = search;
        this.filters = filters;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Predicate toPredicate(Root<InventarioEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        boolean conteo = isCountQuery(query);
        Join<InventarioEntity, AlmacenEntity> almacen = conteo
                ? root.join("almacen", JoinType.LEFT)
                : (Join<InventarioEntity, AlmacenEntity>) root.<InventarioEntity, AlmacenEntity>fetch("almacen", JoinType.LEFT);
        Join<InventarioEntity, ProductoBasicoEntity> producto = conteo
                ? root.join("productoBasicoEntity", JoinType.LEFT)
                : (Join<InventarioEntity, ProductoBasicoEntity>) root.<InventarioEntity, ProductoBasicoEntity>fetch("productoBasicoEntity", JoinType.LEFT);
        Join<ProductoBasicoEntity, CategoriaProductoEntity> categoria = conteo
                ? producto.join("categoriaProductoEntity", JoinType.LEFT)
                : (Join<ProductoBasicoEntity, CategoriaProductoEntity>) producto.<ProductoBasicoEntity, CategoriaProductoEntity>fetch("categoriaProductoEntity", JoinType.LEFT);
        Join<InventarioEntity, UnidadesMedidaEntity> unidad = conteo
                ? root.join("unidadDeMedida", JoinType.LEFT)
                : (Join<InventarioEntity, UnidadesMedidaEntity>) root.<InventarioEntity, UnidadesMedidaEntity>fetch("unidadDeMedida", JoinType.LEFT);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.isFalse(root.get("deleted")));

        if (search != null && !search.isBlank()) {
            predicates.add(cb.or(
                    PagePredicates.like(cb, producto.get("nombre"), search),
                    PagePredicates.like(cb, producto.get("sku"), search),
                    PagePredicates.like(cb, almacen.get("nombre"), search)
            ));
        }

        for (Map.Entry<String, String> entry : filters.entrySet()) {
            if (PagePredicates.blank(entry.getValue())) {
                continue;
            }
            String key = entry.getKey();
            String value = entry.getValue().trim();
            predicates.add(switch (key) {
                case "almacen" -> PagePredicates.like(cb, almacen.get("nombre"), value);
                case "producto" -> PagePredicates.like(cb, producto.get("nombre"), value);
                case "sku" -> PagePredicates.like(cb, producto.get("sku"), value);
                case "categoria" -> PagePredicates.like(cb, categoria.get("nombre"), value);
                case "categoriaPadre" -> categoriaPadre(cb, categoria, value);
                case "unidadMedida", "unidadesMedida" -> PagePredicates.like(cb, unidad.get("nombre"), value);
                case "cantidad" -> PagePredicates.decimalRange(cb, root.get("cantidad"), value);
                case "createdAt" -> PagePredicates.dateTimeRange(cb, root.get("createdAt"), value);
                // La tabla muestra "updatedAt || createdAt": el filtro usa lo mismo.
                case "updatedAt" -> dateTimeRange(cb,
                        cb.coalesce(root.<LocalDateTime>get("updatedAt"), root.<LocalDateTime>get("createdAt")), value);
                default -> throw new FilterNotFound("Invalid filter: filter_" + key);
            });
        }
        return cb.and(predicates.toArray(Predicate[]::new));
    }

    /** La categoria padre solo se muestra cuando las subcategorias estan habilitadas; si no, la columna va vacia. */
    private static Predicate categoriaPadre(CriteriaBuilder cb,
                                            Join<ProductoBasicoEntity, CategoriaProductoEntity> categoria,
                                            String value) {
        if (!SubcategoriaProductoBasico.habilitada()) {
            return cb.disjunction();
        }
        Join<CategoriaProductoEntity, CategoriaProductoEntity> padre = categoria.join("categoriaPadre", JoinType.LEFT);
        return PagePredicates.like(cb, padre.get("nombre"), value);
    }

    private static Predicate dateTimeRange(CriteriaBuilder cb, Expression<LocalDateTime> expr, String value) {
        int index = value.indexOf("..");
        String from = index < 0 ? value.trim() : value.substring(0, index).trim();
        String to = index < 0 ? "" : value.substring(index + 2).trim();
        List<Predicate> predicates = new ArrayList<>();
        if (!from.isBlank()) {
            predicates.add(cb.greaterThanOrEqualTo(expr, parseDate(from).atStartOfDay()));
        }
        if (!to.isBlank()) {
            predicates.add(cb.lessThan(expr, parseDate(to).plusDays(1).atStartOfDay()));
        }
        return predicates.isEmpty() ? cb.conjunction() : cb.and(predicates.toArray(Predicate[]::new));
    }

    private static LocalDate parseDate(String value) {
        try {
            return LocalDate.parse(value.length() > 10 ? value.substring(0, 10) : value);
        } catch (DateTimeParseException ex) {
            throw new FilterNotFound("Invalid filter date: " + value);
        }
    }

    private static boolean isCountQuery(CriteriaQuery<?> query) {
        Class<?> type = query.getResultType();
        return Long.class.equals(type) || long.class.equals(type);
    }
}
