package com.main.tancol.prestamo.util;

import com.main.tancol.common.exeptions.FilterNotFound;
import com.main.tancol.prestamo.entities.EquipoAsignacionEntity;
import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

public class EquipoAsignacionSpecification implements Specification<EquipoAsignacionEntity> {

    @Override
    @Nullable
    public Predicate toPredicate(Root<EquipoAsignacionEntity> root, @Nullable CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        return null;
    }

    public Specification<EquipoAsignacionEntity> getSpecificationByFilters(Map<String, String> params) {
        Specification<EquipoAsignacionEntity> specification = Specification.where(filterByDeleted("false"));

        if (params == null || params.isEmpty()) {
            return specification;
        }

        for (Entry<String, String> entry : params.entrySet()) {
            switch (entry.getKey()) {
                case "id" -> specification = specification.and(filterById(entry.getValue()));
                case "equipo", "idEquipo" -> specification = specification.and(filterByEquipo(entry.getValue()));
                case "cliente", "idCliente" -> specification = specification.and(filterByCliente(entry.getValue()));
                case "activa" -> specification = specification.and(filterByActiva(entry.getValue()));
                case "enabled" -> specification = specification.and(filterByEnabled(entry.getValue()));
                default -> throw new FilterNotFound("Invalid filter: " + entry.getKey());
            }
        }
        return specification;
    }

    private Specification<EquipoAsignacionEntity> filterById(String id) {
        return (root, query, cb) -> cb.equal(root.<UUID>get("id"), UUID.fromString(id));
    }

    private Specification<EquipoAsignacionEntity> filterByEquipo(String idEquipo) {
        return (root, query, cb) -> cb.equal(root.get("equipo").get("id"), UUID.fromString(idEquipo));
    }

    private Specification<EquipoAsignacionEntity> filterByCliente(String idCliente) {
        return (root, query, cb) -> cb.equal(root.get("cliente").get("id"), UUID.fromString(idCliente));
    }

    private Specification<EquipoAsignacionEntity> filterByActiva(String activa) {
        return (root, query, cb) -> cb.equal(root.<Boolean>get("activa"), Boolean.parseBoolean(activa));
    }

    private Specification<EquipoAsignacionEntity> filterByEnabled(String enabled) {
        return (root, query, cb) -> cb.equal(root.<Boolean>get("enabled"), Boolean.parseBoolean(enabled));
    }

    private Specification<EquipoAsignacionEntity> filterByDeleted(String deleted) {
        return (root, query, cb) -> cb.equal(root.<Boolean>get("deleted"), Boolean.parseBoolean(deleted));
    }
}
