package com.main.tancol.orden.util;

import com.main.tancol.common.exceptions.exeption.FilterNotFound;
import com.main.tancol.common.specification.PagePredicates;
import com.main.tancol.orden.entities.OrdenEntity;
import com.main.tancol.orden.enums.FacturacionOrdenEstatus;
import com.main.tancol.orden.enums.Prioridad;
import com.main.tancol.orden.enums.RecepcionEstatus;
import com.main.tancol.orden.enums.SolicitudEstatus;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrdenPageSpecification implements Specification<OrdenEntity> {
    public enum Stage {
        SOLICITUD,
        FACTURACION,
        RECIBO
    }

    private final Stage stage;
    private final String search;
    private final Map<String, String> filters;

    public OrdenPageSpecification(Stage stage, String search, Map<String, String> filters) {
        this.stage = stage;
        this.search = search;
        this.filters = filters;
    }

    @Override
    public Predicate toPredicate(Root<OrdenEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get("deleted"), false));
        if (stage == Stage.FACTURACION) {
            predicates.add(cb.isNotNull(root.get("facturacionEstado")));
        }
        if (stage == Stage.RECIBO) {
            predicates.add(cb.isNotNull(root.get("recepcionEstatus")));
        }
        if (search != null) {
            String pattern = PagePredicates.contains(search);
            List<Predicate> matches = new ArrayList<>();
            matches.add(cb.like(cb.lower(root.get("folio")), pattern));
            matches.add(cb.like(cb.lower(root.get("proveedor").get("nombreComercial")), pattern));
            if (stage == Stage.SOLICITUD) {
                matches.add(cb.like(cb.lower(root.get("titulo")), pattern));
                matches.add(cb.like(cb.lower(root.get("proveedor").get("informacionFiscal").get("razonSocial")), pattern));
            }
            if (stage == Stage.FACTURACION) {
                matches.add(cb.like(cb.lower(root.get("folioExterno")), pattern));
            }
            predicates.add(cb.or(matches.toArray(Predicate[]::new)));
        }
        for (Map.Entry<String, String> entry : filters.entrySet()) {
            if (PagePredicates.blank(entry.getValue())) {
                continue;
            }
            predicates.add(predicateFor(root, cb, entry.getKey(), entry.getValue()));
        }
        return cb.and(predicates.toArray(Predicate[]::new));
    }

    private Predicate predicateFor(Root<OrdenEntity> root, CriteriaBuilder cb, String key, String value) {
        return switch (key) {
            case "folio" -> PagePredicates.like(cb, root.get("folio"), value);
            case "proveedor" -> PagePredicates.like(cb, root.get("proveedor").get("nombreComercial"), value);
            case "folioExterno" -> PagePredicates.like(cb, root.get("folioExterno"), value);
            case "totalOrden" -> PagePredicates.decimalRange(cb, root.get("totalOrden"), value);
            case "prioridad" -> cb.equal(root.get("prioridad"), PagePredicates.enumOf(Prioridad.class, value, key));
            case "fechaSolicitud" -> PagePredicates.dateTimeRange(cb, root.get(fechaSolicitudPath()), value);
            case "fechaModificacion" -> PagePredicates.dateTimeRange(cb, root.get("updatedAt"), value);
            case "fechaFacturacion" -> PagePredicates.dateTimeRange(cb, root.get("fechaFacturacion"), value);
            case "fechaRecibo" -> PagePredicates.dateTimeRange(cb, root.get("fechaRecibo"), value);
            case "solicitudEstatus" -> cb.equal(root.get("solicitudEstatus"), PagePredicates.enumOf(SolicitudEstatus.class, value, key));
            case "estatusFactura" -> cb.equal(root.get("facturacionEstado"), PagePredicates.enumOf(FacturacionOrdenEstatus.class, value, key));
            case "recepcionEstatus" -> cb.equal(root.get("recepcionEstatus"), PagePredicates.enumOf(RecepcionEstatus.class, value, key));
            default -> throw new FilterNotFound("Invalid filter: filter_" + key);
        };
    }

    private String fechaSolicitudPath() {
        return stage == Stage.FACTURACION ? "createdAt" : "getFechaCreacionSolicitud";
    }
}
