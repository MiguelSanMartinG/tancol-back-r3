package com.main.tancol.almacen.util;

import com.main.tancol.almacen.entities.WhitelistProductoEmpleadoEntity;
import com.main.tancol.common.exceptions.exeption.FilterNotFound;
import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.Map;
import java.util.UUID;

public class WhitelistProductoSpecification implements Specification<WhitelistProductoEmpleadoEntity> {

    @SuppressWarnings("null")
    @Nullable
    @Override
    public Predicate toPredicate(Root<WhitelistProductoEmpleadoEntity> root, @Nullable CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        return null;
    }

    public Specification<WhitelistProductoEmpleadoEntity> getSpecificationByFilters(Map<String, String> params) {
        Specification<WhitelistProductoEmpleadoEntity> specification = Specification.where(filterByDeleted("false"));
        if (params == null || params.isEmpty()) {
            return specification;
        }
        for (Map.Entry<String, String> entry : params.entrySet()) {
            switch (entry.getKey()) {
                case "id" -> specification = specification.and(filterById(entry.getValue()));
                case "idEmpleado" -> specification = specification.and(filterByEmpleado(entry.getValue()));
                case "idProducto" -> specification = specification.and(filterByProducto(entry.getValue()));
                case "idDepartamento" -> specification = specification.and(filterByDepartamento(entry.getValue()));
                case "enabled" -> specification = specification.and(filterByEnabled(entry.getValue()));
                case "nombre" -> specification = specification.and(filterByNombreProducto(entry.getValue()));
                default -> throw new FilterNotFound("Invalid filter: " + entry.getKey());
            }
        }
        return specification;
    }

    private Specification<WhitelistProductoEmpleadoEntity> filterById(String id) {
        return (root, query, cb) -> cb.equal(root.get("id"), UUID.fromString(id));
    }

    private Specification<WhitelistProductoEmpleadoEntity> filterByEmpleado(String id) {
        return (root, query, cb) -> cb.equal(root.get("empleado").get("id"), UUID.fromString(id));
    }

    private Specification<WhitelistProductoEmpleadoEntity> filterByProducto(String id) {
        return (root, query, cb) -> cb.equal(root.get("producto").get("id"), UUID.fromString(id));
    }

    private Specification<WhitelistProductoEmpleadoEntity> filterByDepartamento(String id) {
        return (root, query, cb) -> cb.equal(root.get("empleado").get("departamento").get("id"), UUID.fromString(id));
    }

    private Specification<WhitelistProductoEmpleadoEntity> filterByEnabled(String enabled) {
        return (root, query, cb) -> cb.equal(root.get("enabled"), Boolean.parseBoolean(enabled));
    }

    private Specification<WhitelistProductoEmpleadoEntity> filterByNombreProducto(String nombre) {
        return (root, query, cb) -> {
            Join<?, ?> producto = root.join("producto");
            return cb.like(cb.lower(producto.get("nombre")), "%" + nombre.toLowerCase() + "%");
        };
    }

    private Specification<WhitelistProductoEmpleadoEntity> filterByDeleted(String deleted) {
        return (root, query, cb) -> cb.equal(root.get("deleted"), Boolean.parseBoolean(deleted));
    }
}
