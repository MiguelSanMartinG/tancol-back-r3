package com.main.tancol.almacen.util;

import com.main.tancol.almacen.entities.OrdenInternaEntity;
import com.main.tancol.almacen.enums.EstadoOrdenInterna;
import com.main.tancol.common.exceptions.exeption.FilterNotFound;
import com.main.tancol.common.specification.PagePredicates;
import com.main.tancol.empleados.entities.EmpleadosEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Especificacion de GET /orden-interna/page (Salida de material).
 * <p>
 * Emisor y receptor son ManyToOne: LEFT JOIN (fetch en datos, join simple en conteo), sin duplicar filas.
 * Las partidas (coleccion) no se unen en la consulta principal.
 */
public class OrdenInternaPageSpecification implements Specification<OrdenInternaEntity> {

    private final String search;
    private final Map<String, String> filters;

    public OrdenInternaPageSpecification(String search, Map<String, String> filters) {
        this.search = search;
        this.filters = filters;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Predicate toPredicate(Root<OrdenInternaEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        boolean conteo = isCountQuery(query);
        Join<OrdenInternaEntity, EmpleadosEntity> emisor = conteo
                ? root.join("empleadoEnvia", JoinType.LEFT)
                : (Join<OrdenInternaEntity, EmpleadosEntity>) root.<OrdenInternaEntity, EmpleadosEntity>fetch("empleadoEnvia", JoinType.LEFT);
        Join<OrdenInternaEntity, EmpleadosEntity> receptor = conteo
                ? root.join("empleadoRecibe", JoinType.LEFT)
                : (Join<OrdenInternaEntity, EmpleadosEntity>) root.<OrdenInternaEntity, EmpleadosEntity>fetch("empleadoRecibe", JoinType.LEFT);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.isFalse(root.get("deleted")));

        if (search != null && !search.isBlank()) {
            predicates.add(cb.or(
                    PagePredicates.like(cb, root.get("folio"), search),
                    empleadoTexto(cb, emisor, search),
                    empleadoTexto(cb, receptor, search)
            ));
        }

        for (Map.Entry<String, String> entry : filters.entrySet()) {
            if (PagePredicates.blank(entry.getValue())) {
                continue;
            }
            String key = entry.getKey();
            String value = entry.getValue().trim();
            predicates.add(switch (key) {
                case "folio" -> PagePredicates.like(cb, root.get("folio"), value);
                case "emisor" -> empleadoTexto(cb, emisor, value);
                case "receptor" -> empleadoTexto(cb, receptor, value);
                case "createdAt" -> PagePredicates.dateTimeRange(cb, root.get("createdAt"), value);
                case "fechaElaboracion" -> PagePredicates.dateTimeRange(cb, root.get("fechaElaboracion"), value);
                case "estado" -> root.get("estadoOrdenInterna").in(estados(value, key));
                default -> throw new FilterNotFound("Invalid filter: filter_" + key);
            });
        }
        return cb.and(predicates.toArray(Predicate[]::new));
    }

    /** "Contiene" sobre el nombre completo (nombre + apellidos) o el usuario del empleado. */
    static Predicate empleadoTexto(CriteriaBuilder cb, Join<?, EmpleadosEntity> empleado, String value) {
        return cb.or(
                PagePredicates.like(cb, nombreCompleto(cb, empleado), value),
                PagePredicates.like(cb, cb.coalesce(empleado.<String>get("nombreUsuario"), ""), value)
        );
    }

    static Expression<String> nombreCompleto(CriteriaBuilder cb, Join<?, EmpleadosEntity> empleado) {
        Expression<String> nombre = cb.coalesce(empleado.<String>get("nombre"), "");
        Expression<String> paterno = cb.coalesce(empleado.<String>get("primerApellido"), "");
        Expression<String> materno = cb.coalesce(empleado.<String>get("segundoApellido"), "");
        return cb.concat(cb.concat(cb.concat(cb.concat(nombre, " "), paterno), " "), materno);
    }

    /** Uno o varios estados separados por coma; acepta 'EN USO' / 'en_uso' / 'EN_USO'. */
    private static List<EstadoOrdenInterna> estados(String value, String key) {
        List<EstadoOrdenInterna> out = new ArrayList<>();
        for (String raw : value.split(",")) {
            String t = raw.trim().toUpperCase(Locale.ROOT).replaceAll("\\s+", "_");
            if (!t.isEmpty()) {
                out.add(PagePredicates.enumOf(EstadoOrdenInterna.class, t, key));
            }
        }
        if (out.isEmpty()) {
            throw new FilterNotFound("Invalid filter: filter_" + key);
        }
        return out;
    }

    static boolean isCountQuery(CriteriaQuery<?> query) {
        Class<?> type = query.getResultType();
        return Long.class.equals(type) || long.class.equals(type);
    }
}
