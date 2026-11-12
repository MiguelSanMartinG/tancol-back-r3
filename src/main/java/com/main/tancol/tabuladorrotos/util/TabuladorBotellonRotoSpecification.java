package com.main.tancol.tabuladorrotos.util;

import com.main.tancol.common.exceptions.exeption.FilterNotFound;
import com.main.tancol.tabuladorrotos.entities.TabuladorBotellonRotoEntity;
import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.Map;
import java.util.UUID;

public class TabuladorBotellonRotoSpecification implements Specification<TabuladorBotellonRotoEntity> {

    @Override
    @Nullable
    public Predicate toPredicate(Root<TabuladorBotellonRotoEntity> root,
                                 @Nullable CriteriaQuery<?> query,
                                 CriteriaBuilder criteriaBuilder) {
        return null;
    }

    public Specification<TabuladorBotellonRotoEntity> getSpecificationByFilters(Map<String, String> params) {
        Specification<TabuladorBotellonRotoEntity> specification = Specification.where(filterByDeleted("false"));

        if (params == null || params.isEmpty()) {
            return specification;
        }

        for (Map.Entry<String, String> entry : params.entrySet()) {
            switch (entry.getKey()) {
                case "id" -> specification = specification.and(filterById(entry.getValue()));
                case "empleado", "idEmpleado" -> specification = specification.and(filterByEmpleado(entry.getValue()));
                case "producto", "idProducto" -> specification = specification.and(filterByProducto(entry.getValue()));
                case "enabled" -> specification = specification.and(filterByEnabled(entry.getValue()));
                default -> throw new FilterNotFound("Invalid filter: " + entry.getKey());
            }
        }
        return specification;
    }

    private Specification<TabuladorBotellonRotoEntity> filterById(String id) {
        return (root, query, cb) -> cb.equal(root.<UUID>get("id"), UUID.fromString(id));
    }

    private Specification<TabuladorBotellonRotoEntity> filterByEmpleado(String idEmpleado) {
        return (root, query, cb) -> cb.equal(root.get("empleado").get("id"), UUID.fromString(idEmpleado));
    }

    private Specification<TabuladorBotellonRotoEntity> filterByProducto(String idProducto) {
        return (root, query, cb) -> cb.equal(root.get("producto").get("id"), UUID.fromString(idProducto));
    }

    private Specification<TabuladorBotellonRotoEntity> filterByEnabled(String enabled) {
        return (root, query, cb) -> cb.equal(root.<Boolean>get("enabled"), Boolean.parseBoolean(enabled));
    }

    private Specification<TabuladorBotellonRotoEntity> filterByDeleted(String deleted) {
        return (root, query, cb) -> cb.equal(root.<Boolean>get("deleted"), Boolean.parseBoolean(deleted));
    }
}
