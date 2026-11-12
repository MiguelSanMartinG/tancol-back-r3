package com.main.tancol.cliente.util;

import com.main.tancol.cliente.entities.ClienteEntity;
import com.main.tancol.common.exceptions.exeption.FilterNotFound;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
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
                    cb.like(cb.lower(root.get("idCliente").as(String.class)), pattern),
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

    private Predicate predicateFor(Root<ClienteEntity> root, CriteriaBuilder cb, String key, String value) {
        return switch (key) {
            case "noCliente" -> cb.like(cb.lower(root.get("idCliente").as(String.class)), contains(value));
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
