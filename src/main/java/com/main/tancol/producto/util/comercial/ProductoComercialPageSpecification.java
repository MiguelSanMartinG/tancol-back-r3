package com.main.tancol.producto.util.comercial;

import com.main.tancol.common.exceptions.exeption.FilterNotFound;
import com.main.tancol.common.specification.PagePredicates;
import com.main.tancol.producto.entities.comerciales.ProductoComercialEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProductoComercialPageSpecification implements Specification<ProductoComercialEntity> {
    private final String search;
    private final Map<String, String> filters;

    public ProductoComercialPageSpecification(String search, Map<String, String> filters) {
        this.search = search;
        this.filters = filters;
    }

    @Override
    public Predicate toPredicate(Root<ProductoComercialEntity> root, jakarta.persistence.criteria.CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get("deleted"), false));
        if (search != null) {
            String pattern = PagePredicates.contains(search);
            predicates.add(cb.or(
                    cb.like(cb.lower(root.get("nombre")), pattern),
                    cb.like(cb.lower(root.get("sku")), pattern),
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

    private Predicate predicateFor(Root<ProductoComercialEntity> root, CriteriaBuilder cb, String key, String value) {
        return switch (key) {
            case "sku" -> PagePredicates.like(cb, root.get("sku"), value);
            case "nombre" -> PagePredicates.like(cb, root.get("nombre"), value);
            case "alias" -> PagePredicates.like(cb, root.get("alias"), value);
            case "presentacion" -> PagePredicates.longRange(cb, root.get("presentacion"), value);
            case "precio" -> PagePredicates.decimalRange(cb, root.get("precio"), value);
            case "enabled" -> cb.equal(root.get("enabled"), PagePredicates.bool(value, key));
            case "createdAt" -> PagePredicates.dateTimeRange(cb, root.get("createdAt"), value);
            case "updatedAt" -> PagePredicates.dateTimeRange(cb, root.get("updatedAt"), value);
            default -> throw new FilterNotFound("Invalid filter: filter_" + key);
        };
    }
}
