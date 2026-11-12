package com.main.tancol.prestamo.util;

import com.main.tancol.common.exceptions.exeption.FilterNotFound;
import com.main.tancol.prestamo.entities.EquipoAsignacionEntity;
import com.main.tancol.prestamo.entities.EquipoEntity;
import com.main.tancol.prestamo.enums.EstadoEquipo;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class EquipoPageSpecification implements Specification<EquipoEntity> {
    private final String search;
    private final Map<String, String> filters;

    public EquipoPageSpecification(String search, Map<String, String> filters) {
        this.search = search;
        this.filters = filters;
    }

    @Override
    public Predicate toPredicate(Root<EquipoEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get("deleted"), false));
        if (search != null && !search.isBlank()) {
            String pattern = contains(search);
            predicates.add(cb.or(
                    cb.like(cb.lower(root.get("nombre")), pattern),
                    cb.like(cb.lower(root.get("sku")), pattern),
                    cb.like(cb.lower(root.get("marca")), pattern),
                    cb.like(cb.lower(root.get("modelo")), pattern),
                    cb.like(cb.lower(root.get("tipoEquipo").get("nombre")), pattern),
                    clienteNombre(root, query, cb, search)
            ));
        }
        for (Map.Entry<String, String> entry : filters.entrySet()) {
            if (entry.getValue() == null || entry.getValue().isBlank()) {
                continue;
            }
            predicates.add(predicateFor(root, query, cb, entry.getKey(), entry.getValue()));
        }
        return cb.and(predicates.toArray(Predicate[]::new));
    }

    private Predicate predicateFor(Root<EquipoEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb, String key, String value) {
        return switch (key) {
            case "nombre" -> cb.like(cb.lower(root.get("nombre")), contains(value));
            case "sku" -> cb.like(cb.lower(root.get("sku")), contains(value));
            case "marca" -> cb.like(cb.lower(root.get("marca")), contains(value));
            case "modelo" -> cb.like(cb.lower(root.get("modelo")), contains(value));
            case "idTipoEquipo" -> cb.equal(root.get("tipoEquipo").get("id"), parseUuid(value, "filter_idTipoEquipo"));
            case "estado" -> cb.equal(root.get("estado"), parseEstado(value));
            case "cliente" -> clienteNombre(root, query, cb, value);
            case "createdAt" -> dateRange(root, cb, "createdAt", value);
            case "updatedAt" -> dateRange(root, cb, "updatedAt", value);
            default -> throw new FilterNotFound("Invalid filter: filter_" + key);
        };
    }

    private Predicate clienteNombre(Root<EquipoEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb, String value) {
        if (query == null) {
            return cb.disjunction();
        }
        Subquery<Integer> sub = query.subquery(Integer.class);
        Root<EquipoAsignacionEntity> asignacion = sub.from(EquipoAsignacionEntity.class);
        sub.select(cb.literal(1));
        sub.where(
                cb.equal(asignacion.get("equipo"), root),
                cb.isTrue(asignacion.get("activa")),
                cb.equal(asignacion.get("deleted"), false),
                cb.like(cb.lower(asignacion.get("cliente").get("nombreComercial")), contains(value))
        );
        return cb.exists(sub);
    }

    private Predicate dateRange(Root<EquipoEntity> root, CriteriaBuilder cb, String attribute, String value) {
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

    private static UUID parseUuid(String value, String key) {
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException ex) {
            throw new FilterNotFound("Invalid filter: " + key);
        }
    }

    private static EstadoEquipo parseEstado(String value) {
        try {
            return EstadoEquipo.valueOf(value.trim());
        } catch (IllegalArgumentException ex) {
            throw new FilterNotFound("Invalid filter: filter_estado");
        }
    }

    private static String contains(String raw) {
        String cleaned = raw.toLowerCase(Locale.ROOT).replace("%", "").replace("_", "");
        return "%" + cleaned + "%";
    }
}
