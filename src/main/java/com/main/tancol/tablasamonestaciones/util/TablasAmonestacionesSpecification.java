package com.main.tancol.tablasamonestaciones.util;

import com.main.tancol.common.exceptions.exeption.FilterNotFound;
import com.main.tancol.tablasamonestaciones.entities.TablasAmonestacionesEntity;
import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.Map;
import java.util.UUID;

public class TablasAmonestacionesSpecification implements Specification<TablasAmonestacionesEntity> {

    @Override
    @Nullable
    public Predicate toPredicate(Root<TablasAmonestacionesEntity> root,
                                 @Nullable CriteriaQuery<?> query,
                                 CriteriaBuilder criteriaBuilder) {
        return null;
    }

    public Specification<TablasAmonestacionesEntity> getSpecificationByFilters(Map<String, String> params) {
        Specification<TablasAmonestacionesEntity> specification = Specification.where(filterByDeleted("false"));

        if (params == null || params.isEmpty()) {
            return specification;
        }

        for (Map.Entry<String, String> entry : params.entrySet()) {
            switch (entry.getKey()) {
                case "id" -> specification = specification.and(filterById(entry.getValue()));
                case "producto", "idProducto" -> specification = specification.and(filterByProducto(entry.getValue()));
                case "enabled" -> specification = specification.and(filterByEnabled(entry.getValue()));
                default -> throw new FilterNotFound("Invalid filter: " + entry.getKey());
            }
        }
        return specification;
    }

    private Specification<TablasAmonestacionesEntity> filterById(String id) {
        return (root, query, cb) -> cb.equal(root.<UUID>get("id"), UUID.fromString(id));
    }

    private Specification<TablasAmonestacionesEntity> filterByProducto(String idProducto) {
        return (root, query, cb) -> cb.equal(root.get("producto").get("id"), UUID.fromString(idProducto));
    }

    private Specification<TablasAmonestacionesEntity> filterByEnabled(String enabled) {
        return (root, query, cb) -> cb.equal(root.<Boolean>get("enabled"), Boolean.parseBoolean(enabled));
    }

    private Specification<TablasAmonestacionesEntity> filterByDeleted(String deleted) {
        return (root, query, cb) -> cb.equal(root.<Boolean>get("deleted"), Boolean.parseBoolean(deleted));
    }
}
