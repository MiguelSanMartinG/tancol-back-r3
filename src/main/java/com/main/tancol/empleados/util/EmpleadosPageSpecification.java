package com.main.tancol.empleados.util;

import com.main.tancol.common.exceptions.exeption.FilterNotFound;
import com.main.tancol.common.specification.PagePredicates;
import com.main.tancol.empleados.entities.EmpleadosEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EmpleadosPageSpecification implements Specification<EmpleadosEntity> {
    private final String search;
    private final Map<String, String> filters;

    public EmpleadosPageSpecification(String search, Map<String, String> filters) {
        this.search = search;
        this.filters = filters;
    }

    @Override
    public Predicate toPredicate(Root<EmpleadosEntity> root, jakarta.persistence.criteria.CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get("deleted"), false));
        if (search != null) {
            String pattern = PagePredicates.contains(search);
            predicates.add(cb.or(
                    cb.like(cb.lower(root.get("nombre")), pattern),
                    cb.like(cb.lower(root.get("primerApellido")), pattern),
                    cb.like(cb.lower(root.get("segundoApellido")), pattern),
                    cb.like(cb.lower(root.get("correo")), pattern),
                    cb.like(cb.lower(root.get("telefono")), pattern),
                    cb.like(cb.lower(root.get("nombreUsuario")), pattern)
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

    private Predicate predicateFor(Root<EmpleadosEntity> root, CriteriaBuilder cb, String key, String value) {
        return switch (key) {
            case "nombreCompleto" -> cb.or(
                    PagePredicates.like(cb, root.get("nombre"), value),
                    PagePredicates.like(cb, root.get("primerApellido"), value),
                    PagePredicates.like(cb, root.get("segundoApellido"), value)
            );
            case "tipoEmpleado" -> PagePredicates.like(cb, root.get("tiposEmpleados").get("nombre"), value);
            case "departamento" -> PagePredicates.like(cb, root.get("departamento").get("nombre"), value);
            case "role" -> PagePredicates.like(cb, root.get("role").get("name"), value);
            case "enabled" -> cb.equal(root.get("enabled"), PagePredicates.bool(value, key));
            case "createdAt" -> PagePredicates.dateTimeRange(cb, root.get("createdAt"), value);
            case "updatedAt" -> PagePredicates.dateTimeRange(cb, root.get("updatedAt"), value);
            default -> throw new FilterNotFound("Invalid filter: filter_" + key);
        };
    }
}
