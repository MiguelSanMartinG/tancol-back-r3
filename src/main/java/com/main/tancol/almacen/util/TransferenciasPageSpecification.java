package com.main.tancol.almacen.util;

import com.main.tancol.almacen.entities.AlmacenEntity;
import com.main.tancol.almacen.entities.TransferenciasEntity;
import com.main.tancol.almacen.entities.partidas.PartidaTransferenciaEntity;
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
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Especificacion de GET /transferencias/page.
 * <p>
 * El empleado (ManyToOne) va con LEFT JOIN (fetch en datos, join simple en conteo). Almacen base y destino
 * viven en las partidas (coleccion): se filtran con EXISTS y se ordenan con una subconsulta escalar
 * (MIN del nombre), nunca con join en la consulta principal, para que la pagina y el total sean correctos.
 */
public class TransferenciasPageSpecification implements Specification<TransferenciasEntity> {

    /** Ordenes que no son una ruta de atributo y se calculan con subconsulta. */
    public enum OrdenCalculado {BASE, DESTINO}

    private final String search;
    private final Map<String, String> filters;
    private final OrdenCalculado ordenCalculado;
    private final Sort.Direction direccionCalculada;

    public TransferenciasPageSpecification(String search, Map<String, String> filters,
                                           OrdenCalculado ordenCalculado, Sort.Direction direccionCalculada) {
        this.search = search;
        this.filters = filters;
        this.ordenCalculado = ordenCalculado;
        this.direccionCalculada = direccionCalculada;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Predicate toPredicate(Root<TransferenciasEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        boolean conteo = OrdenInternaPageSpecification.isCountQuery(query);
        Join<TransferenciasEntity, EmpleadosEntity> empleado = conteo
                ? root.join("empleado", JoinType.LEFT)
                : (Join<TransferenciasEntity, EmpleadosEntity>) root.<TransferenciasEntity, EmpleadosEntity>fetch("empleado", JoinType.LEFT);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.isFalse(root.get("deleted")));

        if (search != null && !search.isBlank()) {
            predicates.add(cb.or(
                    existsAlmacen(root, query, cb, "almacenOrigen", search),
                    existsAlmacen(root, query, cb, "almacenDestino", search),
                    OrdenInternaPageSpecification.empleadoTexto(cb, empleado, search),
                    PagePredicates.like(cb, cb.coalesce(root.<String>get("motivo"), ""), search)
            ));
        }

        for (Map.Entry<String, String> entry : filters.entrySet()) {
            if (PagePredicates.blank(entry.getValue())) {
                continue;
            }
            String key = entry.getKey();
            String value = entry.getValue().trim();
            predicates.add(switch (key) {
                case "base" -> existsAlmacen(root, query, cb, "almacenOrigen", value);
                case "destino" -> existsAlmacen(root, query, cb, "almacenDestino", value);
                case "entrego" -> OrdenInternaPageSpecification.empleadoTexto(cb, empleado, value);
                case "entregoel" -> PagePredicates.dateTimeRange(cb, root.get("fechaTransferencia"), value);
                default -> throw new FilterNotFound("Invalid filter: filter_" + key);
            });
        }

        if (ordenCalculado != null && !conteo) {
            Expression<String> expr = nombreAlmacen(root, query, cb,
                    ordenCalculado == OrdenCalculado.BASE ? "almacenOrigen" : "almacenDestino");
            query.orderBy(
                    direccionCalculada == Sort.Direction.ASC ? cb.asc(expr) : cb.desc(expr),
                    cb.desc(root.get("fechaTransferencia")),
                    cb.asc(root.get("idTransferencia"))
            );
        }

        return cb.and(predicates.toArray(Predicate[]::new));
    }

    /** EXISTS partida de la transferencia cuyo almacen (origen o destino) contiene el texto. */
    private static Predicate existsAlmacen(Root<TransferenciasEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb,
                                           String atributo, String value) {
        Subquery<Integer> sq = query.subquery(Integer.class);
        Root<PartidaTransferenciaEntity> p = sq.from(PartidaTransferenciaEntity.class);
        Join<PartidaTransferenciaEntity, AlmacenEntity> almacen = p.join(atributo);
        sq.select(cb.literal(1)).where(
                cb.equal(p.get("padrePartida"), root),
                PagePredicates.like(cb, almacen.get("nombre"), value)
        );
        return cb.exists(sq);
    }

    /** Nombre del almacen (origen o destino) de las partidas; MIN para tener un solo valor por transferencia. */
    private static Expression<String> nombreAlmacen(Root<TransferenciasEntity> root, CriteriaQuery<?> query,
                                                    CriteriaBuilder cb, String atributo) {
        Subquery<String> sq = query.subquery(String.class);
        Root<PartidaTransferenciaEntity> p = sq.from(PartidaTransferenciaEntity.class);
        Join<PartidaTransferenciaEntity, AlmacenEntity> almacen = p.join(atributo);
        sq.select(cb.least(almacen.<String>get("nombre"))).where(cb.equal(p.get("padrePartida"), root));
        return sq;
    }
}
