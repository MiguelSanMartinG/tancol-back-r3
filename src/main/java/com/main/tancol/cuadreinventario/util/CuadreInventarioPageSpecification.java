package com.main.tancol.cuadreinventario.util;

import com.main.tancol.almacen.entities.AlmacenEntity;
import com.main.tancol.common.exceptions.exeption.FilterNotFound;
import com.main.tancol.common.specification.PagePredicates;
import com.main.tancol.cuadreinventario.entities.CuadreInventarioEntity;
import com.main.tancol.cuadreinventario.entities.CuadreInventarioPartidaEntity;
import com.main.tancol.cuadreinventario.enums.EstadoCuadre;
import com.main.tancol.cuadreinventario.enums.TipoCuadre;
import com.main.tancol.empleados.entities.EmpleadosEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * Especificacion de GET /cuadre-inventario/page.
 * <p>
 * Almacen, creadoPor y cerradoPor son ManyToOne: LEFT JOIN (fetch en datos, join simple en conteo), sin
 * duplicar filas. Las partidas (coleccion) solo se usan en subconsultas (partidas con diferencia).
 */
public class CuadreInventarioPageSpecification implements Specification<CuadreInventarioEntity> {

    /** Ordenes que no son una ruta de atributo y se calculan con subconsulta. */
    public enum OrdenCalculado {PARTIDAS_CON_DIFERENCIA}

    private final String search;
    private final Map<String, String> filters;
    private final OrdenCalculado ordenCalculado;
    private final Sort.Direction direccionCalculada;

    public CuadreInventarioPageSpecification(String search, Map<String, String> filters,
                                             OrdenCalculado ordenCalculado, Sort.Direction direccionCalculada) {
        this.search = search;
        this.filters = filters;
        this.ordenCalculado = ordenCalculado;
        this.direccionCalculada = direccionCalculada;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Predicate toPredicate(Root<CuadreInventarioEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        boolean conteo = isCountQuery(query);
        Join<CuadreInventarioEntity, AlmacenEntity> almacen = conteo
                ? root.join("almacen", JoinType.LEFT)
                : (Join<CuadreInventarioEntity, AlmacenEntity>) root.<CuadreInventarioEntity, AlmacenEntity>fetch("almacen", JoinType.LEFT);
        Join<CuadreInventarioEntity, EmpleadosEntity> creadoPor = conteo
                ? root.join("creadoPor", JoinType.LEFT)
                : (Join<CuadreInventarioEntity, EmpleadosEntity>) root.<CuadreInventarioEntity, EmpleadosEntity>fetch("creadoPor", JoinType.LEFT);
        Join<CuadreInventarioEntity, EmpleadosEntity> cerradoPor = conteo
                ? root.join("cerradoPor", JoinType.LEFT)
                : (Join<CuadreInventarioEntity, EmpleadosEntity>) root.<CuadreInventarioEntity, EmpleadosEntity>fetch("cerradoPor", JoinType.LEFT);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.isFalse(root.get("deleted")));

        if (search != null && !search.isBlank()) {
            predicates.add(cb.or(
                    PagePredicates.like(cb, cb.coalesce(root.<String>get("folio"), ""), search),
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
                case "folio" -> PagePredicates.like(cb, root.get("folio"), value);
                case "almacen" -> almacen(cb, almacen, value, key);
                case "estado" -> root.get("estado").in(estados(value, key));
                case "tipo" -> root.get("tipo").in(tipos(value, key));
                case "creadoPor" -> empleadoTexto(cb, creadoPor, value);
                case "cerradoPor" -> empleadoTexto(cb, cerradoPor, value);
                case "fecha", "fechaCreacion" -> PagePredicates.dateTimeRange(cb, root.get("fechaCreacion"), value);
                case "fechaCierre" -> PagePredicates.dateTimeRange(cb, root.get("fechaCierre"), value);
                case "ajusteAplicado", "ajuste" -> ajuste(root, cb, value, key);
                case "partidasConDiferencia" -> longRange(cb, partidasConDiferencia(root, query, cb), value);
                default -> throw new FilterNotFound("Invalid filter: filter_" + key);
            });
        }

        if (ordenCalculado != null && !conteo) {
            Expression<Long> expr = partidasConDiferencia(root, query, cb);
            query.orderBy(
                    direccionCalculada == Sort.Direction.ASC ? cb.asc(expr) : cb.desc(expr),
                    cb.desc(root.get("fechaCreacion")),
                    cb.asc(root.get("id"))
            );
        }

        return cb.and(predicates.toArray(Predicate[]::new));
    }

    /** Uno o varios UUID separados por coma (select con id) o texto "contiene" sobre el nombre. */
    private static Predicate almacen(CriteriaBuilder cb, Join<CuadreInventarioEntity, AlmacenEntity> almacen,
                                     String value, String key) {
        List<String> tokens = tokens(value);
        if (!tokens.isEmpty() && tokens.stream().allMatch(CuadreInventarioPageSpecification::esUuid)) {
            return almacen.get("id").in(tokens.stream().map(t -> PagePredicates.uuid(t, key)).toList());
        }
        return PagePredicates.like(cb, almacen.get("nombre"), value);
    }

    /**
     * true = cerrado con ajuste; false = cerrado sin ajuste. Igual que la columna, que muestra "—" en los
     * abiertos (su ajusteAplicado siempre es false y no deben salir al filtrar "No").
     */
    private static Predicate ajuste(Root<CuadreInventarioEntity> root, CriteriaBuilder cb, String value, String key) {
        boolean aplicado = PagePredicates.bool(value, key);
        return cb.and(
                cb.equal(root.get("ajusteAplicado"), aplicado),
                cb.equal(root.get("estado"), EstadoCuadre.CERRADO)
        );
    }

    /** Nombre completo (nombre + apellidos) o usuario, igual que lo arma CuadreInventarioService.nombre(). */
    private static Predicate empleadoTexto(CriteriaBuilder cb, Join<?, EmpleadosEntity> empleado, String value) {
        Expression<String> nombre = cb.coalesce(empleado.<String>get("nombre"), "");
        Expression<String> paterno = cb.coalesce(empleado.<String>get("primerApellido"), "");
        Expression<String> materno = cb.coalesce(empleado.<String>get("segundoApellido"), "");
        Expression<String> completo = cb.concat(cb.concat(cb.concat(cb.concat(nombre, " "), paterno), " "), materno);
        return cb.or(
                PagePredicates.like(cb, completo, value),
                PagePredicates.like(cb, cb.coalesce(empleado.<String>get("nombreUsuario"), ""), value)
        );
    }

    /** Partidas capturadas cuya cantidad fisica difiere de la del sistema (lo que cuenta toResponse). */
    private static Subquery<Long> partidasConDiferencia(Root<CuadreInventarioEntity> root, CriteriaQuery<?> query,
                                                        CriteriaBuilder cb) {
        Subquery<Long> sq = query.subquery(Long.class);
        Root<CuadreInventarioPartidaEntity> p = sq.from(CuadreInventarioPartidaEntity.class);
        sq.select(cb.count(p)).where(
                cb.equal(p.get("cuadre"), root),
                cb.isNotNull(p.get("cantidadFisica")),
                cb.notEqual(p.get("cantidadFisica"), p.get("cantidadSistema"))
        );
        return sq;
    }

    private static Predicate longRange(CriteriaBuilder cb, Expression<Long> expr, String value) {
        int index = value.indexOf("..");
        String from = index < 0 ? value.trim() : value.substring(0, index).trim();
        String to = index < 0 ? "" : value.substring(index + 2).trim();
        List<Predicate> predicates = new ArrayList<>();
        if (!from.isBlank()) {
            predicates.add(cb.greaterThanOrEqualTo(expr, parseLong(from)));
        }
        if (!to.isBlank()) {
            predicates.add(cb.lessThanOrEqualTo(expr, parseLong(to)));
        }
        return predicates.isEmpty() ? cb.conjunction() : cb.and(predicates.toArray(Predicate[]::new));
    }

    private static long parseLong(String value) {
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException ex) {
            try {
                return new java.math.BigDecimal(value.trim()).longValue();
            } catch (NumberFormatException ex2) {
                throw new FilterNotFound("Invalid filter: " + value);
            }
        }
    }

    private static List<EstadoCuadre> estados(String value, String key) {
        List<EstadoCuadre> out = new ArrayList<>();
        for (String t : tokens(value)) {
            out.add(PagePredicates.enumOf(EstadoCuadre.class, t.toUpperCase(Locale.ROOT), key));
        }
        if (out.isEmpty()) {
            throw new FilterNotFound("Invalid filter: filter_" + key);
        }
        return out;
    }

    /** Uno o varios tipos separados por coma (igual que estado). Los cuadres sin tipo no coinciden. */
    private static List<TipoCuadre> tipos(String value, String key) {
        List<TipoCuadre> out = new ArrayList<>();
        for (String t : tokens(value)) {
            out.add(PagePredicates.enumOf(TipoCuadre.class, t.toUpperCase(Locale.ROOT), key));
        }
        if (out.isEmpty()) {
            throw new FilterNotFound("Invalid filter: filter_" + key);
        }
        return out;
    }

    private static List<String> tokens(String value) {
        List<String> out = new ArrayList<>();
        for (String raw : value.split(",")) {
            String t = raw.trim();
            if (!t.isEmpty()) {
                out.add(t);
            }
        }
        return out;
    }

    private static boolean esUuid(String value) {
        try {
            UUID.fromString(value);
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    private static boolean isCountQuery(CriteriaQuery<?> query) {
        Class<?> type = query.getResultType();
        return Long.class.equals(type) || long.class.equals(type);
    }
}
