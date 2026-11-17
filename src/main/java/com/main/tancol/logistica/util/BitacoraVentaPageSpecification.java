package com.main.tancol.logistica.util;

import com.main.tancol.common.exceptions.exeption.FilterNotFound;
import com.main.tancol.common.specification.PagePredicates;
import com.main.tancol.logistica.entities.bitacora.BitacoraVentaRutaEntity;
import com.main.tancol.logistica.entities.bitacora.TrayectoEntity;
import com.main.tancol.logistica.enums.EstatusBitacoraVenta;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Especificacion de la pagina de bitacoras de ventas (GET /bitacoras-rutas/page).
 * Los trayectos (partidas) son coleccion: el conteo, su filtro y su orden van por subconsulta correlacionada.
 */
public class BitacoraVentaPageSpecification implements Specification<BitacoraVentaRutaEntity> {
    private final String search;
    private final Map<String, String> filters;
    /** Direccion del orden por numero de partidas; null si no aplica. */
    private final Sort.Direction partidasSortDirection;

    public BitacoraVentaPageSpecification(String search, Map<String, String> filters) {
        this(search, filters, null);
    }

    public BitacoraVentaPageSpecification(String search, Map<String, String> filters, Sort.Direction partidasSortDirection) {
        this.search = search;
        this.filters = filters;
        this.partidasSortDirection = partidasSortDirection;
    }

    @Override
    public Predicate toPredicate(Root<BitacoraVentaRutaEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();
        if (search != null && !search.isBlank()) {
            predicates.add(PagePredicates.like(cb, root.get("folio"), search));
        }
        for (Map.Entry<String, String> entry : filters.entrySet()) {
            if (PagePredicates.blank(entry.getValue())) {
                continue;
            }
            predicates.add(predicateFor(root, query, cb, entry.getKey(), entry.getValue().trim()));
        }
        if (partidasSortDirection != null && !isCountQuery(query)) {
            Subquery<Long> partidas = numeroPartidas(root, query, cb);
            query.orderBy(
                    partidasSortDirection == Sort.Direction.ASC ? cb.asc(partidas) : cb.desc(partidas),
                    cb.desc(root.get("createdAt")),
                    cb.asc(root.get("idBitacora"))
            );
        }
        if (predicates.isEmpty()) {
            return cb.conjunction();
        }
        return cb.and(predicates.toArray(Predicate[]::new));
    }

    private Predicate predicateFor(Root<BitacoraVentaRutaEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb,
                                   String key, String value) {
        return switch (key) {
            case "fecha" -> dateRange(cb, root.get("fecha"), value);
            case "createdAt" -> PagePredicates.dateTimeRange(cb, root.get("createdAt"), value);
            case "updatedAt" -> PagePredicates.dateTimeRange(cb, root.get("updatedAt"), value);
            case "folio" -> PagePredicates.like(cb, root.get("folio"), value);
            case "status" -> root.get("estatusBitacoraVenta").in(estatus(value, key));
            case "partidas" -> longRange(cb, numeroPartidas(root, query, cb), value);
            default -> throw new FilterNotFound("Invalid filter: filter_" + key);
        };
    }

    public static Subquery<Long> numeroPartidas(Root<BitacoraVentaRutaEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        Subquery<Long> sq = query.subquery(Long.class);
        Root<TrayectoEntity> t = sq.from(TrayectoEntity.class);
        sq.select(cb.count(t)).where(cb.equal(t.get("bitacoraOrigen"), root));
        return sq;
    }

    private static boolean isCountQuery(CriteriaQuery<?> query) {
        Class<?> type = query.getResultType();
        return Long.class.equals(type) || long.class.equals(type);
    }

    /** Rango 'desde..hasta' (ambos inclusive) sobre una columna DATE. */
    private static Predicate dateRange(CriteriaBuilder cb, Path<LocalDate> path, String value) {
        String[] parts = splitRange(value);
        List<Predicate> predicates = new ArrayList<>();
        if (!parts[0].isBlank()) {
            predicates.add(cb.greaterThanOrEqualTo(path, parseDate(parts[0])));
        }
        if (!parts[1].isBlank()) {
            predicates.add(cb.lessThanOrEqualTo(path, parseDate(parts[1])));
        }
        return predicates.isEmpty() ? cb.conjunction() : cb.and(predicates.toArray(Predicate[]::new));
    }

    private static Predicate longRange(CriteriaBuilder cb, Expression<Long> expr, String value) {
        String[] parts = splitRange(value);
        List<Predicate> predicates = new ArrayList<>();
        if (!parts[0].isBlank()) {
            predicates.add(cb.greaterThanOrEqualTo(expr, parseLong(parts[0])));
        }
        if (!parts[1].isBlank()) {
            predicates.add(cb.lessThanOrEqualTo(expr, parseLong(parts[1])));
        }
        return predicates.isEmpty() ? cb.conjunction() : cb.and(predicates.toArray(Predicate[]::new));
    }

    private static List<EstatusBitacoraVenta> estatus(String value, String key) {
        List<EstatusBitacoraVenta> out = new ArrayList<>();
        for (String raw : value.split(",")) {
            if (!raw.isBlank()) {
                out.add(PagePredicates.enumOf(EstatusBitacoraVenta.class, raw.trim().toUpperCase(), key));
            }
        }
        if (out.isEmpty()) {
            throw new FilterNotFound("Invalid filter: filter_" + key);
        }
        return out;
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

    private static long parseLong(String value) {
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException ex) {
            throw new FilterNotFound("Invalid filter: " + value);
        }
    }
}
