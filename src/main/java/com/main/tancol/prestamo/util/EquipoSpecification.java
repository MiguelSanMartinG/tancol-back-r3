package com.main.tancol.prestamo.util;

import com.main.tancol.common.exeptions.FilterNotFound;
import com.main.tancol.prestamo.entities.EquipoEntity;
import com.main.tancol.prestamo.enums.EstadoEquipo;
import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

public class EquipoSpecification implements Specification<EquipoEntity> {

    @Override
    @Nullable
    public Predicate toPredicate(Root<EquipoEntity> root, @Nullable CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        return null;
    }

    public Specification<EquipoEntity> getSpecificationByFilters(Map<String, String> params) {
        Specification<EquipoEntity> specification = Specification.where(filterByDeleted("false"));

        if (params == null || params.isEmpty()) {
            return specification;
        }

        for (Entry<String, String> entry : params.entrySet()) {
            switch (entry.getKey()) {
                case "id" -> specification = specification.and(filterById(entry.getValue()));
                case "nombre" -> specification = specification.and(filterByNombre(entry.getValue()));
                case "sku" -> specification = specification.and(filterBySku(entry.getValue()));
                case "estado" -> specification = specification.and(filterByEstado(entry.getValue()));
                case "tipo", "idTipoEquipo" -> specification = specification.and(filterByTipo(entry.getValue()));
                case "cliente", "idCliente" -> specification = specification.and(filterByClienteActivo(entry.getValue()));
                case "enabled" -> specification = specification.and(filterByEnabled(entry.getValue()));
                default -> throw new FilterNotFound("Invalid filter: " + entry.getKey());
            }
        }
        return specification;
    }

    private Specification<EquipoEntity> filterById(String id) {
        return (root, query, cb) -> cb.equal(root.<UUID>get("id"), UUID.fromString(id));
    }

    private Specification<EquipoEntity> filterByNombre(String nombre) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("nombre")), "%" + nombre.toLowerCase() + "%");
    }

    private Specification<EquipoEntity> filterBySku(String sku) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("sku")), "%" + sku.toLowerCase() + "%");
    }

    private Specification<EquipoEntity> filterByEstado(String estado) {
        return (root, query, cb) -> cb.equal(root.get("estado"), EstadoEquipo.valueOf(estado.toUpperCase()));
    }

    private Specification<EquipoEntity> filterByTipo(String idTipo) {
        return (root, query, cb) -> cb.equal(root.get("tipoEquipo").get("id"), UUID.fromString(idTipo));
    }

    private Specification<EquipoEntity> filterByClienteActivo(String idCliente) {
        return (root, query, cb) -> {
            if (query != null) {
                query.distinct(true);
            }
            Join<Object, Object> asignaciones = root.join("asignaciones", JoinType.INNER);
            return cb.and(
                    cb.equal(asignaciones.get("cliente").get("id"), UUID.fromString(idCliente)),
                    cb.isTrue(asignaciones.get("activa")),
                    cb.isFalse(asignaciones.get("deleted"))
            );
        };
    }

    private Specification<EquipoEntity> filterByEnabled(String enabled) {
        return (root, query, cb) -> cb.equal(root.<Boolean>get("enabled"), Boolean.parseBoolean(enabled));
    }

    private Specification<EquipoEntity> filterByDeleted(String deleted) {
        return (root, query, cb) -> cb.equal(root.<Boolean>get("deleted"), Boolean.parseBoolean(deleted));
    }
}
