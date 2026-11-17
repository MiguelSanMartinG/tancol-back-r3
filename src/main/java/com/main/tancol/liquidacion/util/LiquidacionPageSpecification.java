package com.main.tancol.liquidacion.util;

import com.main.tancol.common.exceptions.exeption.FilterNotFound;
import com.main.tancol.common.specification.PagePredicates;
import com.main.tancol.empleados.entities.EmpleadosEntity;
import com.main.tancol.liquidacion.entities.LiquidacionEntity;
import com.main.tancol.liquidacion.entities.LiquidacionPartidaEntity;
import com.main.tancol.liquidacion.entities.LiquidacionTotalizadoEmpleadoEntity;
import com.main.tancol.liquidacion.entities.LiquidacionTotalizadoEntity;
import com.main.tancol.liquidacion.enums.EstadosLiquidacion;
import com.main.tancol.logistica.entities.bitacora.TrayectoEntity;
import com.main.tancol.logistica.entities.rutas.RutasEntity;
import com.main.tancol.logistica.entities.rutas.VehiculosEntity;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;

/**
 * Especificacion de la pagina de liquidaciones (GET /liquidacion/page).
 * <p>
 * Ruta y chofer de la ruta son ManyToOne: se resuelven con LEFT JOIN (fetch en la consulta de datos,
 * join simple en la de conteo), sin duplicar filas. Todo lo que vive en colecciones (partidas,
 * totalizados, empleados del totalizado) se resuelve con subconsultas correlacionadas / EXISTS,
 * nunca con join en la consulta principal, para que la pagina y el total sean correctos.
 * <p>
 * Los filtros ruta / chofer / ayudante / vehiculo aceptan dos formas:
 * <ul>
 *     <li>lista de UUID separados por coma (selects multiples del bloque superior);</li>
 *     <li>texto libre (filtro de columna), que busca "contiene" sobre el nombre.</li>
 * </ul>
 * Las llaves {@code rutaIds}, {@code choferIds}, {@code ayudanteIds} y {@code vehiculoIds} solo aceptan
 * UUIDs y existen para que el bloque superior no choque con el filtro de texto de la columna.
 */
public class LiquidacionPageSpecification implements Specification<LiquidacionEntity> {

    /** Ordenes que no son una ruta de atributo y se calculan con subconsulta. */
    public enum OrdenCalculado {PARTIDAS, TOTAL_ESTIMADO}

    private final String search;
    private final Map<String, String> filters;
    private final OrdenCalculado ordenCalculado;
    private final Sort.Direction direccionCalculada;

    public LiquidacionPageSpecification(String search, Map<String, String> filters) {
        this(search, filters, null, null);
    }

    public LiquidacionPageSpecification(String search, Map<String, String> filters,
                                        OrdenCalculado ordenCalculado, Sort.Direction direccionCalculada) {
        this.search = search;
        this.filters = filters;
        this.ordenCalculado = ordenCalculado;
        this.direccionCalculada = direccionCalculada;
    }

    @Override
    public Predicate toPredicate(Root<LiquidacionEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        boolean conteo = isCountQuery(query);
        Join<LiquidacionEntity, RutasEntity> ruta = rutaJoin(root, conteo);
        Join<RutasEntity, EmpleadosEntity> chofer = choferJoin(ruta, conteo);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.isFalse(root.get("deleted")));

        if (search != null && !search.isBlank()) {
            predicates.add(cb.or(
                    PagePredicates.like(cb, root.get("folio"), search),
                    rutaTexto(cb, ruta, search),
                    choferTexto(cb, chofer, search)
            ));
        }

        for (Map.Entry<String, String> entry : filters.entrySet()) {
            if (PagePredicates.blank(entry.getValue())) {
                continue;
            }
            predicates.add(predicateFor(root, ruta, chofer, query, cb, entry.getKey(), entry.getValue().trim()));
        }

        if (ordenCalculado != null && !conteo) {
            Expression<?> expr = ordenCalculado == OrdenCalculado.PARTIDAS
                    ? numeroPartidas(root, query, cb)
                    : totalEstimado(root, query, cb);
            query.orderBy(
                    direccionCalculada == Sort.Direction.ASC ? cb.asc(expr) : cb.desc(expr),
                    cb.desc(root.get("fecha")),
                    cb.asc(root.get("idLiquidacion"))
            );
        }

        return cb.and(predicates.toArray(Predicate[]::new));
    }

    private Predicate predicateFor(Root<LiquidacionEntity> root,
                                   Join<LiquidacionEntity, RutasEntity> ruta,
                                   Join<RutasEntity, EmpleadosEntity> chofer,
                                   CriteriaQuery<?> query,
                                   CriteriaBuilder cb,
                                   String key,
                                   String value) {
        return switch (key) {
            case "fecha" -> PagePredicates.dateTimeRange(cb, root.get("fecha"), value);
            case "createdAt" -> PagePredicates.dateTimeRange(cb, root.get("createdAt"), value);
            case "updatedAt" -> PagePredicates.dateTimeRange(cb, root.get("updatedAt"), value);
            case "folio" -> PagePredicates.like(cb, root.get("folio"), value);
            case "bloqueNota" -> PagePredicates.like(cb, root.get("bloqueNota"), value);
            case "estatus" -> root.get("estadosLiquidacion").in(estatus(value, key));
            case "partidas" -> longRange(cb, numeroPartidas(root, query, cb), value);
            case "totalEstimado" -> decimalRange(cb, totalEstimado(root, query, cb), value);

            case "ruta" -> esListaUuid(value)
                    ? ruta.get("id").in(uuids(value, key))
                    : rutaTexto(cb, ruta, value);
            case "rutaIds" -> ruta.get("id").in(uuids(value, key));

            case "chofer" -> esListaUuid(value)
                    ? choferPorIds(root, chofer, query, cb, uuids(value, key))
                    : choferTexto(cb, chofer, value);
            case "choferIds" -> choferPorIds(root, chofer, query, cb, uuids(value, key));

            case "ayudante" -> esListaUuid(value)
                    ? existsAyudante(root, query, cb, uuids(value, key))
                    : existsAyudante(root, query, cb, value);
            case "ayudanteIds" -> existsAyudante(root, query, cb, uuids(value, key));

            case "vehiculo" -> esListaUuid(value)
                    ? vehiculoPorIds(root, ruta, query, cb, uuids(value, key))
                    : vehiculoTexto(ruta, cb, value);
            case "vehiculoIds" -> vehiculoPorIds(root, ruta, query, cb, uuids(value, key));

            default -> throw new FilterNotFound("Invalid filter: filter_" + key);
        };
    }

    // ---------------------------------------------------------------- joins

    @SuppressWarnings("unchecked")
    private static Join<LiquidacionEntity, RutasEntity> rutaJoin(Root<LiquidacionEntity> root, boolean conteo) {
        if (conteo) {
            return root.join("ruta", JoinType.LEFT);
        }
        // Fetch: la fila trae ruta en la misma consulta (evita N+1). Hibernate implementa Fetch y Join en el mismo objeto.
        return (Join<LiquidacionEntity, RutasEntity>) root.<LiquidacionEntity, RutasEntity>fetch("ruta", JoinType.LEFT);
    }

    @SuppressWarnings("unchecked")
    private static Join<RutasEntity, EmpleadosEntity> choferJoin(Join<LiquidacionEntity, RutasEntity> ruta, boolean conteo) {
        if (conteo) {
            return ruta.join("chofer", JoinType.LEFT);
        }
        return (Join<RutasEntity, EmpleadosEntity>) ruta.<RutasEntity, EmpleadosEntity>fetch("chofer", JoinType.LEFT);
    }

    // ---------------------------------------------------------------- ruta / chofer / vehiculo

    private static Predicate rutaTexto(CriteriaBuilder cb, Join<LiquidacionEntity, RutasEntity> ruta, String value) {
        return cb.or(
                PagePredicates.like(cb, ruta.get("nombre"), value),
                PagePredicates.like(cb, ruta.get("numero"), value)
        );
    }

    private static Predicate choferTexto(CriteriaBuilder cb, Join<?, EmpleadosEntity> empleado, String value) {
        return cb.or(
                PagePredicates.like(cb, nombreCompleto(cb, empleado), value),
                PagePredicates.like(cb, cb.coalesce(empleado.<String>get("nombreUsuario"), ""), value)
        );
    }

    private static Expression<String> nombreCompleto(CriteriaBuilder cb, Join<?, EmpleadosEntity> empleado) {
        Expression<String> nombre = cb.coalesce(empleado.<String>get("nombre"), "");
        Expression<String> paterno = cb.coalesce(empleado.<String>get("primerApellido"), "");
        Expression<String> materno = cb.coalesce(empleado.<String>get("segundoApellido"), "");
        return cb.concat(cb.concat(cb.concat(cb.concat(nombre, " "), paterno), " "), materno);
    }

    /** Chofer base de la ruta (lo que muestra la tabla) o chofer registrado en los totalizados de la liquidacion. */
    private static Predicate choferPorIds(Root<LiquidacionEntity> root,
                                          Join<RutasEntity, EmpleadosEntity> chofer,
                                          CriteriaQuery<?> query,
                                          CriteriaBuilder cb,
                                          List<UUID> ids) {
        return cb.or(
                chofer.get("id").in(ids),
                existsTotalizadoEmpleado(root, query, cb, true, (emp, b) -> emp.get("id").in(ids))
        );
    }

    private static Predicate existsAyudante(Root<LiquidacionEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb, List<UUID> ids) {
        return existsTotalizadoEmpleado(root, query, cb, false, (emp, b) -> emp.get("id").in(ids));
    }

    private static Predicate existsAyudante(Root<LiquidacionEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb, String texto) {
        return existsTotalizadoEmpleado(root, query, cb, false, (emp, b) -> choferTexto(b, emp, texto));
    }

    /** Vehiculo base de la ruta o vehiculo de algun trayecto (bitacora) que alimento la liquidacion. */
    private static Predicate vehiculoPorIds(Root<LiquidacionEntity> root,
                                            Join<LiquidacionEntity, RutasEntity> ruta,
                                            CriteriaQuery<?> query,
                                            CriteriaBuilder cb,
                                            List<UUID> ids) {
        Subquery<Integer> sq = query.subquery(Integer.class);
        Root<LiquidacionTotalizadoEntity> tot = sq.from(LiquidacionTotalizadoEntity.class);
        Join<LiquidacionTotalizadoEntity, TrayectoEntity> trayecto = tot.join("trayecto");
        Join<TrayectoEntity, VehiculosEntity> vehiculo = trayecto.join("vehiculo");
        sq.select(cb.literal(1)).where(
                cb.equal(tot.get("liquidacionOrigen"), root),
                vehiculo.get("id").in(ids)
        );
        return cb.or(ruta.get("vehiculo").get("id").in(ids), cb.exists(sq));
    }

    private static Predicate vehiculoTexto(Join<LiquidacionEntity, RutasEntity> ruta, CriteriaBuilder cb, String value) {
        Join<RutasEntity, VehiculosEntity> vehiculo = ruta.join("vehiculo", JoinType.LEFT);
        return cb.or(
                PagePredicates.like(cb, vehiculo.get("nombre"), value),
                PagePredicates.like(cb, vehiculo.get("placas"), value)
        );
    }

    /**
     * EXISTS sobre los empleados de los totalizados de la liquidacion.
     * Choferes: {@code fue_chofer = true}. Ayudantes: {@code fue_chofer} false/null; el ayudante puede colgar
     * directamente del totalizado o solo de su chofer ({@code choferOrigen}), por eso se revisan ambos caminos.
     */
    private static Predicate existsTotalizadoEmpleado(
            Root<LiquidacionEntity> root,
            CriteriaQuery<?> query,
            CriteriaBuilder cb,
            boolean fueChofer,
            BiFunction<Join<LiquidacionTotalizadoEmpleadoEntity, EmpleadosEntity>, CriteriaBuilder, Predicate> condicion
    ) {
        Subquery<Integer> sq = query.subquery(Integer.class);
        Root<LiquidacionTotalizadoEmpleadoEntity> te = sq.from(LiquidacionTotalizadoEmpleadoEntity.class);
        Join<LiquidacionTotalizadoEmpleadoEntity, EmpleadosEntity> empleado = te.join("empleado");
        Join<LiquidacionTotalizadoEmpleadoEntity, LiquidacionTotalizadoEntity> tot = te.join("origenTotalizado", JoinType.LEFT);

        Predicate rol;
        Predicate pertenece;
        if (fueChofer) {
            rol = cb.isTrue(te.get("chofer"));
            pertenece = cb.equal(tot.get("liquidacionOrigen"), root);
        } else {
            rol = cb.or(cb.isNull(te.get("chofer")), cb.isFalse(te.get("chofer")));
            Join<LiquidacionTotalizadoEmpleadoEntity, LiquidacionTotalizadoEmpleadoEntity> choferOrigen =
                    te.join("choferOrigen", JoinType.LEFT);
            Join<LiquidacionTotalizadoEmpleadoEntity, LiquidacionTotalizadoEntity> totChofer =
                    choferOrigen.join("origenTotalizado", JoinType.LEFT);
            pertenece = cb.or(
                    cb.equal(tot.get("liquidacionOrigen"), root),
                    cb.equal(totChofer.get("liquidacionOrigen"), root)
            );
        }
        sq.select(cb.literal(1)).where(rol, pertenece, condicion.apply(empleado, cb));
        return cb.exists(sq);
    }

    // ---------------------------------------------------------------- partidas (subconsultas)

    /** Numero de partidas de la liquidacion (todas, igual que la lista anterior). */
    public static Subquery<Long> numeroPartidas(Root<LiquidacionEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        Subquery<Long> sq = query.subquery(Long.class);
        Root<LiquidacionPartidaEntity> p = sq.from(LiquidacionPartidaEntity.class);
        sq.select(cb.count(p)).where(cb.equal(p.get("liquidacionOrigen"), root));
        return sq;
    }

    /** Total estimado = suma(precio * cantidad) de las partidas, igual que calculaba el front. */
    public static Expression<BigDecimal> totalEstimado(Root<LiquidacionEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        Subquery<BigDecimal> sq = query.subquery(BigDecimal.class);
        Root<LiquidacionPartidaEntity> p = sq.from(LiquidacionPartidaEntity.class);
        Expression<BigDecimal> precio = cb.coalesce(p.<BigDecimal>get("precio"), BigDecimal.ZERO);
        Expression<BigDecimal> cantidad = cb.coalesce(p.<BigDecimal>get("cantidad"), BigDecimal.ZERO);
        sq.select(cb.sum(cb.prod(precio, cantidad))).where(cb.equal(p.get("liquidacionOrigen"), root));
        return cb.coalesce(sq, BigDecimal.ZERO);
    }

    // ---------------------------------------------------------------- helpers

    private static boolean isCountQuery(CriteriaQuery<?> query) {
        Class<?> type = query.getResultType();
        return Long.class.equals(type) || long.class.equals(type);
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

    private static boolean esListaUuid(String value) {
        List<String> tokens = tokens(value);
        if (tokens.isEmpty()) {
            return false;
        }
        for (String t : tokens) {
            try {
                UUID.fromString(t);
            } catch (IllegalArgumentException ex) {
                return false;
            }
        }
        return true;
    }

    private static List<UUID> uuids(String value, String key) {
        List<UUID> out = new ArrayList<>();
        for (String t : tokens(value)) {
            out.add(PagePredicates.uuid(t, key));
        }
        if (out.isEmpty()) {
            throw new FilterNotFound("Invalid filter: filter_" + key);
        }
        return out;
    }

    private static List<EstadosLiquidacion> estatus(String value, String key) {
        List<EstadosLiquidacion> out = new ArrayList<>();
        for (String t : tokens(value)) {
            out.add(PagePredicates.enumOf(EstadosLiquidacion.class, t.toUpperCase(), key));
        }
        if (out.isEmpty()) {
            throw new FilterNotFound("Invalid filter: filter_" + key);
        }
        return out;
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

    private static Predicate decimalRange(CriteriaBuilder cb, Expression<BigDecimal> expr, String value) {
        String[] parts = splitRange(value);
        List<Predicate> predicates = new ArrayList<>();
        if (!parts[0].isBlank()) {
            predicates.add(cb.greaterThanOrEqualTo(expr, parseDecimal(parts[0])));
        }
        if (!parts[1].isBlank()) {
            predicates.add(cb.lessThanOrEqualTo(expr, parseDecimal(parts[1])));
        }
        return predicates.isEmpty() ? cb.conjunction() : cb.and(predicates.toArray(Predicate[]::new));
    }

    private static String[] splitRange(String value) {
        int index = value.indexOf("..");
        if (index < 0) {
            return new String[]{value.trim(), ""};
        }
        return new String[]{value.substring(0, index).trim(), value.substring(index + 2).trim()};
    }

    private static long parseLong(String value) {
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException ex) {
            throw new FilterNotFound("Invalid filter: " + value);
        }
    }

    private static BigDecimal parseDecimal(String value) {
        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException ex) {
            throw new FilterNotFound("Invalid filter: " + value);
        }
    }
}
