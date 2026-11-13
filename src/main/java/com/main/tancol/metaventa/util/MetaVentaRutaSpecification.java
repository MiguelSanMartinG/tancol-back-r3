package com.main.tancol.metaventa.util;

import com.main.tancol.common.exeptions.FilterNotFound;
import com.main.tancol.metaventa.entities.MetaVentaRutaEntity;
import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

public class MetaVentaRutaSpecification implements Specification<MetaVentaRutaEntity> {

    @Override
    @Nullable
    public Predicate toPredicate(Root<MetaVentaRutaEntity> root, @Nullable CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        return null;
    }

    public Specification<MetaVentaRutaEntity> getSpecificationByFilters(Map<String, String> params) {
        Specification<MetaVentaRutaEntity> specification = Specification.where(filterByDeleted("false"));

        if (params == null || params.isEmpty()) {
            return specification;
        }

        for (Entry<String, String> entry : params.entrySet()) {
            switch (entry.getKey()) {
                case "id" -> specification = specification.and(filterById(entry.getValue()));
                case "ruta", "idRuta" -> specification = specification.and(filterByRuta(entry.getValue()));
                case "producto", "idProducto" -> specification = specification.and(filterByProducto(entry.getValue()));
                case "activa" -> specification = specification.and(filterByActiva(entry.getValue()));
                case "enabled" -> specification = specification.and(filterByEnabled(entry.getValue()));
                default -> throw new FilterNotFound("Invalid filter: " + entry.getKey());
            }
        }
        return specification;
    }

    private Specification<MetaVentaRutaEntity> filterById(String id) {
        return (root, query, cb) -> cb.equal(root.<UUID>get("id"), UUID.fromString(id));
    }

    private Specification<MetaVentaRutaEntity> filterByRuta(String idRuta) {
        return (root, query, cb) -> cb.equal(root.get("ruta").get("id"), UUID.fromString(idRuta));
    }

    private Specification<MetaVentaRutaEntity> filterByProducto(String idProducto) {
        return (root, query, cb) -> cb.equal(root.get("producto").get("id"), UUID.fromString(idProducto));
    }

    private Specification<MetaVentaRutaEntity> filterByActiva(String activa) {
        return (root, query, cb) -> cb.equal(root.<Boolean>get("activa"), Boolean.parseBoolean(activa));
    }

    private Specification<MetaVentaRutaEntity> filterByEnabled(String enabled) {
        return (root, query, cb) -> cb.equal(root.<Boolean>get("enabled"), Boolean.parseBoolean(enabled));
    }

    private Specification<MetaVentaRutaEntity> filterByDeleted(String deleted) {
        return (root, query, cb) -> cb.equal(root.<Boolean>get("deleted"), Boolean.parseBoolean(deleted));
    }
}
