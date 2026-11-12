package com.main.tancol.empleados.util;

import com.main.tancol.common.exceptions.exeption.FilterNotFound;
import com.main.tancol.common.specification.PagePredicates;
import com.main.tancol.empleados.entities.DepartamentoEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DepartamentoPageSpecification implements Specification<DepartamentoEntity> {
    private final String search;
    private final Map<String, String> filters;

    public DepartamentoPageSpecification(String search, Map<String, String> filters) {
        this.search = search;
        this.filters = filters;
    }

    @Override
    public Predicate toPredicate(Root<DepartamentoEntity> root, jakarta.persistence.criteria.CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get("deleted"), false));
        if (search != null) {
            String pattern = PagePredicates.contains(search);
            predicates.add(cb.or(
                    cb.like(cb.lower(root.get("nombre")), pattern),
                    cb.like(cb.lower(root.get("alias")), pattern),
                    cb.like(cb.lower(root.get("descripcion")), pattern)
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

    private Predicate predicateFor(Root<DepartamentoEntity> root, CriteriaBuilder cb, String key, String value) {
        return switch (key) {
            case "nombre" -> PagePredicates.like(cb, root.get("nombre"), value);
            case "status" -> cb.equal(root.get("enabled"), PagePredicates.bool(value, key));
            case "createdAt" -> PagePredicates.dateTimeRange(cb, root.get("createdAt"), value);
            case "updatedAt" -> PagePredicates.dateTimeRange(cb, root.get("updatedAt"), value);
            default -> throw new FilterNotFound("Invalid filter: filter_" + key);
        };
    }
}
