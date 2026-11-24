package com.main.tancol.common.specification;

import com.main.tancol.common.exceptions.exeption.FilterNotFound;
import com.main.tancol.common.util.RangoFechas;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public final class PagePredicates {
    private PagePredicates() {
    }

    public static String contains(String raw) {
        String cleaned = raw.toLowerCase(Locale.ROOT).replace("%", "").replace("_", "");
        return "%" + cleaned + "%";
    }

    public static Predicate like(CriteriaBuilder cb, Expression<String> path, String value) {
        return cb.like(cb.lower(path), contains(value));
    }

    /** Rango 'desde..hasta' (ambos inclusive) sobre una columna TIMESTAMP; admite fecha sola o fecha-hora. */
    public static Predicate dateTimeRange(CriteriaBuilder cb, Expression<LocalDateTime> path, String value) {
        RangoFechas rango = rangoFechas(value);
        List<Predicate> predicates = new ArrayList<>();
        if (rango.inicio() != null) {
            predicates.add(cb.greaterThanOrEqualTo(path, rango.inicio()));
        }
        if (rango.fin() != null) {
            predicates.add(cb.lessThanOrEqualTo(path, rango.fin()));
        }
        if (predicates.isEmpty()) {
            return cb.conjunction();
        }
        return cb.and(predicates.toArray(Predicate[]::new));
    }

    /** Rango 'desde..hasta' (ambos inclusive) sobre una columna DATE; de una fecha-hora solo cuenta el dia. */
    public static Predicate dateRange(CriteriaBuilder cb, Expression<LocalDate> path, String value) {
        RangoFechas rango = rangoFechas(value);
        List<Predicate> predicates = new ArrayList<>();
        if (rango.inicio() != null) {
            predicates.add(cb.greaterThanOrEqualTo(path, rango.inicio().toLocalDate()));
        }
        if (rango.fin() != null) {
            predicates.add(cb.lessThanOrEqualTo(path, rango.fin().toLocalDate()));
        }
        if (predicates.isEmpty()) {
            return cb.conjunction();
        }
        return cb.and(predicates.toArray(Predicate[]::new));
    }

    public static Predicate decimalRange(CriteriaBuilder cb, Path<BigDecimal> path, String value) {
        String[] parts = splitRange(value);
        List<Predicate> predicates = new ArrayList<>();
        if (!parts[0].isBlank()) {
            predicates.add(cb.greaterThanOrEqualTo(path, decimal(parts[0])));
        }
        if (!parts[1].isBlank()) {
            predicates.add(cb.lessThanOrEqualTo(path, decimal(parts[1])));
        }
        if (predicates.isEmpty()) {
            return cb.conjunction();
        }
        return cb.and(predicates.toArray(Predicate[]::new));
    }

    public static Predicate longRange(CriteriaBuilder cb, Path<Long> path, String value) {
        String[] parts = splitRange(value);
        List<Predicate> predicates = new ArrayList<>();
        if (!parts[0].isBlank()) {
            predicates.add(cb.greaterThanOrEqualTo(path, longValue(parts[0])));
        }
        if (!parts[1].isBlank()) {
            predicates.add(cb.lessThanOrEqualTo(path, longValue(parts[1])));
        }
        if (predicates.isEmpty()) {
            return cb.conjunction();
        }
        return cb.and(predicates.toArray(Predicate[]::new));
    }

    public static boolean bool(String value, String key) {
        if ("true".equalsIgnoreCase(value.trim())) {
            return true;
        }
        if ("false".equalsIgnoreCase(value.trim())) {
            return false;
        }
        throw new FilterNotFound("Invalid filter: filter_" + key);
    }

    public static UUID uuid(String value, String key) {
        try {
            return UUID.fromString(value.trim());
        } catch (IllegalArgumentException ex) {
            throw new FilterNotFound("Invalid filter: filter_" + key);
        }
    }

    public static <E extends Enum<E>> E enumOf(Class<E> type, String value, String key) {
        try {
            return Enum.valueOf(type, value.trim());
        } catch (IllegalArgumentException ex) {
            throw new FilterNotFound("Invalid filter: filter_" + key);
        }
    }

    public static boolean blank(String value) {
        return value == null || value.isBlank();
    }

    private static String[] splitRange(String value) {
        int index = value.indexOf("..");
        if (index < 0) {
            return new String[]{value.trim(), ""};
        }
        return new String[]{value.substring(0, index).trim(), value.substring(index + 2).trim()};
    }

    private static RangoFechas rangoFechas(String value) {
        String[] parts = splitRange(value);
        try {
            return RangoFechas.parse(parts[0], parts[1]);
        } catch (DateTimeParseException ex) {
            throw new FilterNotFound("Invalid filter date: " + value);
        }
    }

    private static BigDecimal decimal(String value) {
        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException ex) {
            throw new FilterNotFound("Invalid filter: " + value);
        }
    }

    private static long longValue(String value) {
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException ex) {
            throw new FilterNotFound("Invalid filter: " + value);
        }
    }
}
