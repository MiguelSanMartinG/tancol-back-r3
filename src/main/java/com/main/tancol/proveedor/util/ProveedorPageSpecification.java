package com.main.tancol.proveedor.util;

import com.main.tancol.common.exceptions.exeption.FilterNotFound;
import com.main.tancol.proveedor.entity.ProveedorEntity;
import com.main.tancol.proveedor.entity.TipoProveedorEntity;
import com.main.tancol.proveedor.enums.TipoContribuyente;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;

/**
 * Especificacion de la pagina de proveedores.
 * <p>
 * Un proveedor puede tener varios tipos (ManyToMany {@code tiposProveedor}); por eso los filtros y el
 * orden por tipo se resuelven con subconsultas correlacionadas y nunca con un join en la consulta
 * principal, para no duplicar filas en la pagina ni alterar el conteo total.
 */
public class ProveedorPageSpecification implements Specification<ProveedorEntity> {
    private final String search;
    private final Map<String, String> filters;
    /** Direccion del orden por nombre de tipo (primer tipo alfabetico); null si no aplica. */
    private final Sort.Direction tipoSortDirection;

    public ProveedorPageSpecification(String search, Map<String, String> filters) {
        this(search, filters, null);
    }

    public ProveedorPageSpecification(String search, Map<String, String> filters, Sort.Direction tipoSortDirection) {
        this.search = search;
        this.filters = filters;
        this.tipoSortDirection = tipoSortDirection;
    }

    @Override
    public Predicate toPredicate(Root<ProveedorEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get("deleted"), false));
        if (search != null && !search.isBlank()) {
            String pattern = contains(search);
            predicates.add(cb.or(
                    cb.like(cb.lower(root.get("nombreComercial")), pattern),
                    cb.like(cb.lower(root.get("informacionFiscal").get("razonSocial")), pattern),
                    cb.like(cb.lower(root.get("informacionFiscal").get("rfc")), pattern),
                    existsTipo(root, query, cb, (tipo, b) -> b.like(b.lower(tipo.get("nombre")), pattern))
            ));
        }
        for (Map.Entry<String, String> entry : filters.entrySet()) {
            if (entry.getValue() == null || entry.getValue().isBlank()) {
                continue;
            }
            predicates.add(predicateFor(root, query, cb, entry.getKey(), entry.getValue()));
        }
        if (tipoSortDirection != null && !isCountQuery(query)) {
            Subquery<String> primerTipo = query.subquery(String.class);
            Root<ProveedorEntity> sub = primerTipo.from(ProveedorEntity.class);
            Join<ProveedorEntity, TipoProveedorEntity> tipo = sub.join("tiposProveedor");
            primerTipo.select(cb.least(cb.lower(tipo.<String>get("nombre"))))
                    .where(cb.equal(sub, root));
            query.orderBy(
                    tipoSortDirection == Sort.Direction.ASC ? cb.asc(primerTipo) : cb.desc(primerTipo),
                    cb.asc(root.get("id"))
            );
        }
        return cb.and(predicates.toArray(Predicate[]::new));
    }

    private Predicate predicateFor(Root<ProveedorEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb, String key, String value) {
        return switch (key) {
            case "nombreComercial" -> cb.like(cb.lower(root.get("nombreComercial")), contains(value));
            case "razonSocial" -> cb.like(cb.lower(root.get("informacionFiscal").get("razonSocial")), contains(value));
            case "rfc" -> cb.like(cb.lower(root.get("informacionFiscal").get("rfc")), contains(value));
            case "idTipoProveedor" -> {
                UUID id = parseUuid(value);
                yield existsTipo(root, query, cb, (tipo, b) -> b.equal(tipo.get("id"), id));
            }
            case "nombreTipoProveedor" -> {
                String pattern = contains(value);
                yield existsTipo(root, query, cb, (tipo, b) -> b.like(b.lower(tipo.get("nombre")), pattern));
            }
            case "tipoContribuyente" -> cb.equal(
                    root.get("informacionFiscal").get("tipoContribuyenteFiscal"),
                    parseContribuyente(value)
            );
            case "createdAt" -> dateRange(root, cb, "createdAt", value);
            case "updatedAt" -> dateRange(root, cb, "updatedAt", value);
            default -> throw new FilterNotFound("Invalid filter: filter_" + key);
        };
    }

    /** EXISTS (proveedor con algun tipo que cumpla la condicion), sin join en la consulta principal. */
    private static Predicate existsTipo(
            Root<ProveedorEntity> root,
            CriteriaQuery<?> query,
            CriteriaBuilder cb,
            BiFunction<Join<ProveedorEntity, TipoProveedorEntity>, CriteriaBuilder, Predicate> condicion
    ) {
        Subquery<Integer> sq = query.subquery(Integer.class);
        Root<ProveedorEntity> sub = sq.from(ProveedorEntity.class);
        Join<ProveedorEntity, TipoProveedorEntity> tipo = sub.join("tiposProveedor");
        sq.select(cb.literal(1)).where(cb.equal(sub, root), condicion.apply(tipo, cb));
        return cb.exists(sq);
    }

    private static boolean isCountQuery(CriteriaQuery<?> query) {
        Class<?> type = query.getResultType();
        return Long.class.equals(type) || long.class.equals(type);
    }

    private Predicate dateRange(Root<ProveedorEntity> root, CriteriaBuilder cb, String attribute, String value) {
        String[] parts = splitRange(value);
        List<Predicate> predicates = new ArrayList<>();
        if (!parts[0].isBlank()) {
            predicates.add(cb.greaterThanOrEqualTo(root.get(attribute), parseDate(parts[0]).atStartOfDay()));
        }
        if (!parts[1].isBlank()) {
            predicates.add(cb.lessThan(root.get(attribute), parseDate(parts[1]).plusDays(1).atStartOfDay()));
        }
        if (predicates.isEmpty()) {
            return cb.conjunction();
        }
        return cb.and(predicates.toArray(Predicate[]::new));
    }

    private static String[] splitRange(String value) {
        int index = value.indexOf("..");
        if (index < 0) {
            return new String[]{value.trim(), ""};
        }
        return new String[]{value.substring(0, index).trim(), value.substring(index + 2).trim()};
    }

    private static LocalDate parseDate(String value) {
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException ex) {
            throw new FilterNotFound("Invalid filter date: " + value);
        }
    }

    private static UUID parseUuid(String value) {
        try {
            return UUID.fromString(value.trim());
        } catch (IllegalArgumentException ex) {
            throw new FilterNotFound("Invalid filter: filter_idTipoProveedor");
        }
    }

    private static TipoContribuyente parseContribuyente(String value) {
        try {
            return TipoContribuyente.valueOf(value.trim());
        } catch (IllegalArgumentException ex) {
            throw new FilterNotFound("Invalid filter: filter_tipoContribuyente");
        }
    }

    private static String contains(String raw) {
        String cleaned = raw.toLowerCase(Locale.ROOT).replace("%", "").replace("_", "");
        return "%" + cleaned + "%";
    }
}
