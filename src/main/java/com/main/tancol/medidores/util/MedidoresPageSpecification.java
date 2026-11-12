package com.main.tancol.medidores.util;

import com.main.tancol.common.exceptions.exeption.FilterNotFound;
import com.main.tancol.common.specification.PagePredicates;
import com.main.tancol.medidores.entities.MedidoresEntity;
import com.main.tancol.medidores.enums.TiposAgua;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MedidoresPageSpecification implements Specification<MedidoresEntity> {
    private final String search;
    private final Map<String, String> filters;

    public MedidoresPageSpecification(String search, Map<String, String> filters) {
        this.search = search;
        this.filters = filters;
    }

    @Override
    public Predicate toPredicate(Root<MedidoresEntity> root, jakarta.persistence.criteria.CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get("deleted"), false));
        if (search != null) {
            String pattern = PagePredicates.contains(search);
            predicates.add(cb.or(
                    cb.like(cb.lower(root.get("nombre")), pattern),
                    cb.like(cb.lower(root.get("alias")), pattern)
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

    private Predicate predicateFor(Root<MedidoresEntity> root, CriteriaBuilder cb, String key, String value) {
        return switch (key) {
            case "nombre" -> PagePredicates.like(cb, root.get("nombre"), value);
            case "alias" -> PagePredicates.like(cb, root.get("alias"), value);
            case "tipoAgua" -> cb.equal(root.get("tipoAgua"), PagePredicates.enumOf(TiposAgua.class, value, key));
            case "ultimaMedicion" -> PagePredicates.decimalRange(cb, root.get("medicion"), value);
            case "fechaUltimaMedicion" -> PagePredicates.dateTimeRange(cb, root.get("fechaUltimaMedicion"), value);
            case "createdAt" -> PagePredicates.dateTimeRange(cb, root.get("createdAt"), value);
            case "updatedAt" -> PagePredicates.dateTimeRange(cb, root.get("updatedAt"), value);
            default -> throw new FilterNotFound("Invalid filter: filter_" + key);
        };
    }
}
