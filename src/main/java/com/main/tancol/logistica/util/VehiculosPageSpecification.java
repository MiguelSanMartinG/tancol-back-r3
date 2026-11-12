package com.main.tancol.logistica.util;

import com.main.tancol.common.exceptions.exeption.FilterNotFound;
import com.main.tancol.common.specification.PagePredicates;
import com.main.tancol.logistica.entities.rutas.VehiculosEntity;
import com.main.tancol.logistica.enums.EstatusVehiculo;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VehiculosPageSpecification implements Specification<VehiculosEntity> {
    private final String search;
    private final Map<String, String> filters;

    public VehiculosPageSpecification(String search, Map<String, String> filters) {
        this.search = search;
        this.filters = filters;
    }

    @Override
    public Predicate toPredicate(Root<VehiculosEntity> root, jakarta.persistence.criteria.CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get("deleted"), false));
        if (search != null) {
            String pattern = PagePredicates.contains(search);
            predicates.add(cb.or(
                    cb.like(cb.lower(root.get("nombre")), pattern),
                    cb.like(cb.lower(root.get("placas")), pattern)
            ));
        }
        for (Map.Entry<String, String> entry : filters.entrySet()) {
            if (PagePredicates.blank(entry.getValue())) {
                continue;
            }
            predicates.add(predicateFor(root, cb, entry.getKey(), entry.getValue()));
        }
        return cb.and(predicates.toArray(Predicate[]::new));
    }

    private Predicate predicateFor(Root<VehiculosEntity> root, CriteriaBuilder cb, String key, String value) {
        return switch (key) {
            case "nombre" -> PagePredicates.like(cb, root.get("nombre"), value);
            case "placas" -> PagePredicates.like(cb, root.get("placas"), value);
            case "status" -> cb.equal(root.get("estatusVehiculo"), PagePredicates.enumOf(EstatusVehiculo.class, value, key));
            case "capacidadMaxima" -> PagePredicates.decimalRange(cb, root.get("capacidadMaxima"), value);
            case "createdAt" -> PagePredicates.dateTimeRange(cb, root.get("createdAt"), value);
            case "updatedAt" -> PagePredicates.dateTimeRange(cb, root.get("updatedAt"), value);
            default -> throw new FilterNotFound("Invalid filter: filter_" + key);
        };
    }
}
