package com.main.tancol.metaventa.util;

import com.main.tancol.common.exeptions.FilterNotFound;
import com.main.tancol.metaventa.entities.MetaVentaEmpleadoEntity;
import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

public class MetaVentaSpecification implements Specification<MetaVentaEmpleadoEntity> {

    @Override
    @Nullable
    public Predicate toPredicate(Root<MetaVentaEmpleadoEntity> root, @Nullable CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        return null;
    }

    public Specification<MetaVentaEmpleadoEntity> getSpecificationByFilters(Map<String, String> params) {
        Specification<MetaVentaEmpleadoEntity> specification = Specification.where(filterByDeleted("false"));

        if (params == null || params.isEmpty()) {
            return specification;
        }

        for (Entry<String, String> entry : params.entrySet()) {
            switch (entry.getKey()) {
                case "id" -> specification = specification.and(filterById(entry.getValue()));
                case "empleado", "idEmpleado" -> specification = specification.and(filterByEmpleado(entry.getValue()));
                case "producto", "idProducto" -> specification = specification.and(filterByProducto(entry.getValue()));
                case "activa" -> specification = specification.and(filterByActiva(entry.getValue()));
                case "enabled" -> specification = specification.and(filterByEnabled(entry.getValue()));
                default -> throw new FilterNotFound("Invalid filter: " + entry.getKey());
            }
        }
        return specification;
    }

    private Specification<MetaVentaEmpleadoEntity> filterById(String id) {
        return (root, query, cb) -> cb.equal(root.<UUID>get("id"), UUID.fromString(id));
    }

    private Specification<MetaVentaEmpleadoEntity> filterByEmpleado(String idEmpleado) {
        return (root, query, cb) -> cb.equal(root.get("empleado").get("id"), UUID.fromString(idEmpleado));
    }

    private Specification<MetaVentaEmpleadoEntity> filterByProducto(String idProducto) {
        return (root, query, cb) -> cb.equal(root.get("producto").get("id"), UUID.fromString(idProducto));
    }

    private Specification<MetaVentaEmpleadoEntity> filterByActiva(String activa) {
        return (root, query, cb) -> cb.equal(root.<Boolean>get("activa"), Boolean.parseBoolean(activa));
    }

    private Specification<MetaVentaEmpleadoEntity> filterByEnabled(String enabled) {
        return (root, query, cb) -> cb.equal(root.<Boolean>get("enabled"), Boolean.parseBoolean(enabled));
    }

    private Specification<MetaVentaEmpleadoEntity> filterByDeleted(String deleted) {
        return (root, query, cb) -> cb.equal(root.<Boolean>get("deleted"), Boolean.parseBoolean(deleted));
    }
}
