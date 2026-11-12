package com.main.tancol.medidores.util;


import com.main.tancol.common.exeptions.FilterNotFound;
import com.main.tancol.medidores.entities.MedidoresEntity;
import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;



public class MedidoresSpecification implements Specification<MedidoresEntity> {

    @SuppressWarnings("null")
    @Nullable
    @Override
    public Predicate toPredicate(Root<MedidoresEntity> root , @Nullable CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder){
        return null;
    }

     public Specification<MedidoresEntity> getSpecificationByFilters(Map<String,String> params){
        Specification<MedidoresEntity> specification = Specification.where(this.filterByDeleted("false"));

        if (params == null || params.isEmpty()) {
            return specification;
        }

        for(Entry<String, String> entry : params.entrySet()){
            switch (entry.getKey()){
                case "id":
                    specification = specification.and(this.filterById(entry.getValue()));
                    break;
                case "nombre":
                    specification = specification.and(this.filterByName(entry.getValue()));
                    break;
                case "enabled":
                    specification = specification.and(this.filterByEnabled(entry.getValue()));
                    break;
                case "proceso":
                    specification = specification.and(this.filterByProceso(entry.getValue()));
                    break;
                case "padres":
                    specification = specification.and(filterByPadres(entry.getValue()));
                    break;

                default:
                    throw new FilterNotFound("Invalid filter: " + entry.getKey());

            }
        }
        return specification;
    }

    private Specification<MedidoresEntity> filterByProceso(String value) {
        return (root,query, cb) -> cb.equal(root.get("proceso"), value);
    }

    private Specification<MedidoresEntity> filterById(String id){
        return (root,query, cb) -> cb.equal(root.<UUID>get("id"), UUID.fromString(id));
    }

    private Specification<MedidoresEntity> filterByName(String nombre){
        return (root,query, cb) -> cb.like(root.get("nombre"), nombre);
    }

    private Specification<MedidoresEntity> filterByEnabled(String enabled){
        return (root,query, cb) -> cb.equal(root.<Boolean>get("enabled"), Boolean.parseBoolean(enabled));
    }

    private Specification<MedidoresEntity> filterByDeleted(String deleted){
        return (root,query, cb) -> cb.equal(root.<Boolean>get("deleted"), Boolean.parseBoolean(deleted));
    }
    private Specification<MedidoresEntity> filterByPadres(String value) {
        return (root, query, cb) ->
                Boolean.parseBoolean(value)
                        ? cb.isNotNull(root.get("medidorPadre"))
                        : cb.isNull(root.get("medidorPadre"));
    }

}
