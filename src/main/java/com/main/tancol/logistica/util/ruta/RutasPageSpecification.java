package com.main.tancol.logistica.util.ruta;

import com.main.tancol.common.exceptions.exeption.FilterNotFound;
import com.main.tancol.common.specification.PagePredicates;
import com.main.tancol.logistica.entities.rutas.RutasEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RutasPageSpecification implements Specification<RutasEntity> {
    private final String search;
    private final Map<String, String> filters;

    public RutasPageSpecification(String search, Map<String, String> filters) {
        this.search = search;
        this.filters = filters;
    }

    @Override
    public Predicate toPredicate(Root<RutasEntity> root, jakarta.persistence.criteria.CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get("deleted"), false));
        if (search != null) {
            String pattern = PagePredicates.contains(search);
            predicates.add(cb.or(
                    cb.like(cb.lower(root.get("nombre")), pattern),
                    cb.like(cb.lower(root.get("numero")), pattern)
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

    private Predicate predicateFor(Root<RutasEntity> root, CriteriaBuilder cb, String key, String value) {
        return switch (key) {
            case "nombre" -> PagePredicates.like(cb, root.get("nombre"), value);
            case "numero" -> PagePredicates.like(cb, root.get("numero"), value);
            case "chofer" -> cb.or(
                    PagePredicates.like(cb, root.get("chofer").get("nombre"), value),
                    PagePredicates.like(cb, root.get("chofer").get("primerApellido"), value),
                    PagePredicates.like(cb, root.get("chofer").get("segundoApellido"), value)
            );
            case "nota" -> PagePredicates.like(cb, root.get("letraPredeterminada"), value);
            case "vehiculo" -> PagePredicates.like(cb, root.get("vehiculo").get("nombre"), value);
            case "producto" -> PagePredicates.like(cb, root.get("productBase").get("nombre"), value);
            case "enabled" -> cb.equal(root.get("enabled"), PagePredicates.bool(value, key));
            default -> throw new FilterNotFound("Invalid filter: filter_" + key);
        };
    }
}
