package com.main.tancol.cliente.util;

import com.main.tancol.cliente.entities.ClienteEntity;
import com.main.tancol.common.exceptions.exeption.FilterNotFound;
import com.main.tancol.common.specification.PagePredicates;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.query.criteria.JpaExpression;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class ClientePageSpecification implements Specification<ClienteEntity> {
    private final String search;
    private final Map<String, String> filters;

    public ClientePageSpecification(String search, Map<String, String> filters) {
        this.search = search;
        this.filters = filters;
    }

    @Override
    public Predicate toPredicate(Root<ClienteEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get("deleted"), false));
        if (search != null && !search.isBlank()) {
            String pattern = contains(search);
            predicates.add(cb.or(
                    cb.like(cb.lower(root.get("nombreComercial")), pattern),
                    cb.like(cb.lower(root.get("informacionFiscal").get("razonSocial")), pattern),
                    cb.like(cb.lower(root.get("telefono")), pattern),
                    cb.like(cb.lower(numeroComoTexto(root)), pattern),
                    cb.like(cb.lower(root.get("tipoDeCliente").get("nombre")), pattern)
            ));
        }
        for (Map.Entry<String, String> entry : filters.entrySet()) {
            if (entry.getValue() == null || entry.getValue().isBlank()) {
                continue;
            }
            predicates.add(predicateFor(root, cb, entry.getKey(), entry.getValue()));
        }
        return cb.and(predicates.toArray(Predicate[]::new));
    }

    /**
     * Número de cliente (Integer) como texto para buscarlo con LIKE. En Hibernate 6, {@code .as(String.class)}
     * no genera un CAST en SQL y PostgreSQL rechazaba {@code lower(integer)}: la búsqueda fallaba.
     */
    private static Expression<String> numeroComoTexto(Root<ClienteEntity> root) {
        return ((JpaExpression<?>) root.get("idCliente")).cast(String.class);
    }

    private Predicate predicateFor(Root<ClienteEntity> root, CriteriaBuilder cb, String key, String value) {
        return switch (key) {
            case "noCliente" -> cb.like(cb.lower(numeroComoTexto(root)), contains(value));
            case "nombreComercial" -> cb.like(cb.lower(root.get("nombreComercial")), contains(value));
            case "razonSocial" -> cb.like(cb.lower(root.get("informacionFiscal").get("razonSocial")), contains(value));
            case "idTipoDeCliente" -> cb.equal(root.get("tipoDeCliente").get("id"), parseUuid(value));
            case "telefono" -> cb.like(cb.lower(root.get("telefono")), contains(value));
            case "fechaAlta" -> dateRange(root, cb, "fechaAlta", value);
            case "createdAt" -> dateRange(root, cb, "createdAt", value);
            case "updatedAt" -> dateRange(root, cb, "updatedAt", value);
            default -> throw new FilterNotFound("Invalid filter: filter_" + key);
        };
    }

    private Predicate dateRange(Root<ClienteEntity> root, CriteriaBuilder cb, String attribute, String value) {
        return PagePredicates.dateTimeRange(cb, root.get(attribute), value);
    }

    private static UUID parseUuid(String value) {
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException ex) {
            throw new FilterNotFound("Invalid filter: filter_idTipoDeCliente");
        }
    }

    private static String contains(String raw) {
        String cleaned = raw.toLowerCase(Locale.ROOT).replace("%", "").replace("_", "");
        return "%" + cleaned + "%";
    }
}
