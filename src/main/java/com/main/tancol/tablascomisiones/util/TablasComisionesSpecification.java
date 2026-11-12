package com.main.tancol.tablascomisiones.util;

import com.main.tancol.common.exceptions.exeption.FilterNotFound;
import com.main.tancol.tablascomisiones.entities.TablasComisionesEntity;
import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.Map;
import java.util.UUID;

public class TablasComisionesSpecification implements Specification<TablasComisionesEntity> {

    @Override
    @Nullable
    public Predicate toPredicate(Root<TablasComisionesEntity> root,
                                 @Nullable CriteriaQuery<?> query,
                                 CriteriaBuilder criteriaBuilder) {
        return null;
    }

    public Specification<TablasComisionesEntity> getSpecificationByFilters(Map<String, String> params) {
        Specification<TablasComisionesEntity> specification = Specification.where(filterByDeleted("false"));

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

    private Specification<TablasComisionesEntity> filterById(String id) {
        return (root, query, cb) -> cb.equal(root.<UUID>get("id"), UUID.fromString(id));
    }

    private Specification<TablasComisionesEntity> filterByProducto(String idProducto) {
        return (root, query, cb) -> cb.equal(root.get("producto").get("id"), UUID.fromString(idProducto));
    }

    private Specification<TablasComisionesEntity> filterByEnabled(String enabled) {
        return (root, query, cb) -> cb.equal(root.<Boolean>get("enabled"), Boolean.parseBoolean(enabled));
    }

    private Specification<TablasComisionesEntity> filterByDeleted(String deleted) {
        return (root, query, cb) -> cb.equal(root.<Boolean>get("deleted"), Boolean.parseBoolean(deleted));
    }
}
