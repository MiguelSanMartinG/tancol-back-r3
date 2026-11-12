package com.main.tancol.liquidacion.util.conceptos;


import com.main.tancol.common.exeptions.FilterNotFound;
import com.main.tancol.liquidacion.entities.ConceptosVentasEntity;
import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;



public class ConceptosVentasSpecification implements Specification<ConceptosVentasEntity> {

    @SuppressWarnings("null")
    @Nullable
    @Override
    public Predicate toPredicate(Root<ConceptosVentasEntity> root , @Nullable CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder){
        return null;
    }

     public Specification<ConceptosVentasEntity> getSpecificationByFilters(Map<String,String> params){
        Specification<ConceptosVentasEntity> specification = Specification.where(this.filterByDeleted("false"));

        if (params == null || params.isEmpty()) {
            return specification;
        }

        for(Entry<String, String> entry : params.entrySet()){
            switch (entry.getKey()){
                case "id":
                    specification = specification.and(this.filterById(entry.getValue()));
                    break;
                case "name":
                    specification = specification.and(this.filterByName(entry.getValue()));
                    break;
                case "enabled":
                    specification = specification.and(this.filterByEnabled(entry.getValue()));
                    break;
                case "concepto":
                    specification = specification.and(this.filterByConcepto(entry.getValue()));
                    break;

                default:
                    throw new FilterNotFound("Invalid filter: " + entry.getKey());

            }
        }
        return specification;
    }

    private Specification<ConceptosVentasEntity> filterByConcepto(String value) {
        return (root,query, cb) -> cb.like(root.get("concepto"), value);
    }

    private Specification<ConceptosVentasEntity> filterById(String id){
        return (root,query, cb) -> cb.equal(root.<UUID>get("id"), UUID.fromString(id));
    }

    private Specification<ConceptosVentasEntity> filterByName(String nombre){
        return (root,query, cb) -> cb.like(root.get("name"), nombre);
    }

    private Specification<ConceptosVentasEntity> filterByEnabled(String enabled){
        return (root,query, cb) -> cb.equal(root.<Boolean>get("enabled"), Boolean.parseBoolean(enabled));
    }

    private Specification<ConceptosVentasEntity> filterByDeleted(String deleted){
        return (root,query, cb) -> cb.equal(root.<Boolean>get("deleted"), Boolean.parseBoolean(deleted));
    }


}
