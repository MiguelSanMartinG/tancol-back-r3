package com.main.tancol.orden.util;

import com.main.tancol.common.exceptions.exeption.FilterNotFound;
import com.main.tancol.orden.entities.OrdenEntity;
import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.Map;
import java.util.UUID;

public class OrdenSpecification implements Specification<OrdenEntity> {

    @SuppressWarnings("null")
    @Nullable
    @Override
    public Predicate toPredicate(Root<OrdenEntity> root , @Nullable CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder){
        return null;
    }
    public Specification<OrdenEntity> getSpecificationByFilters(Map<String,String> params){
        Specification<OrdenEntity> specification = Specification.where(this.filterByDeleted("false"));

        if (params == null || params.isEmpty()) return specification;

        for(Map.Entry<String, String> entry : params.entrySet()){
            switch (entry.getKey()){
                case "id":
                    specification = specification.and(this.filterById(entry.getValue()));
                    break;
                case "folio":
                    specification = specification.and(this.filterByFolio(entry.getValue()));
                    break;
                case "titulo":
                specification = specification.and(this.filterByTitulo(entry.getValue()));
                    break;
                case "prioridad":
                specification = specification.and(this.filterByPrioridad(entry.getValue()));
                    break;
                case "enabled":
                    specification = specification.and(this.filterByEnabled(entry.getValue()));
                    break;

                default:
                    throw new FilterNotFound("Invalid filter: " + entry.getKey());

            }

        }
        return specification;
    }

    // ID
    private Specification<OrdenEntity> filterById(String id){
        return (root,query, cb) -> cb.equal(root.<UUID>get("id"), UUID.fromString(id));
    }

    // STRINGS
    private Specification<OrdenEntity> filterByFolio(String folio){
        return (root,query, cb) -> cb.like(root.get("folio"), folio);
    }

    private Specification<OrdenEntity> filterByTitulo(String titulo){
        return (root,query, cb) -> cb.like(root.get("titulo"), titulo);
    }
    private Specification<OrdenEntity> filterByPrioridad(String prioridad){
        return (root,query, cb) -> cb.like(root.get("prioridad"), prioridad);
    }

    // Booleans
    private Specification<OrdenEntity> filterByEnabled(String enabled){
        return (root,query, cb) -> cb.equal(root.<Boolean>get("enabled"), Boolean.parseBoolean(enabled));
    }

    private Specification<OrdenEntity> filterByDeleted(String deleted){
        return (root,query, cb) -> cb.equal(root.<Boolean>get("deleted"), Boolean.parseBoolean(deleted));
    }

}
