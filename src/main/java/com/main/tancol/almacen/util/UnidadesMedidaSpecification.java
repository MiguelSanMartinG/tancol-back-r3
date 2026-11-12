package com.main.tancol.almacen.util;


import com.main.tancol.common.exceptions.exeption.FilterNotFound;
import com.main.tancol.almacen.entities.*;
import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.*;
import java.util.Map.Entry;



public class UnidadesMedidaSpecification implements Specification<UnidadesMedidaEntity> {

    @SuppressWarnings("null")
    @Nullable
    @Override
    public Predicate toPredicate(Root<UnidadesMedidaEntity> root , @Nullable CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder){
        return null;
    }

     public Specification<UnidadesMedidaEntity> getSpecificationByFilters(Map<String,String> params){
        Specification<UnidadesMedidaEntity> specification = Specification.where(this.filterByDeleted("false"));

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

                default:
                    throw new FilterNotFound("Invalid filter: " + entry.getKey());

            }
        }
        return specification;
    }

    private Specification<UnidadesMedidaEntity> filterById(String id){
        return (root,query, cb) -> cb.equal(root.<UUID>get("idUnidadesMedida"), UUID.fromString(id));
    }

    private Specification<UnidadesMedidaEntity> filterByName(String nombre){
        return (root,query, cb) -> cb.like(root.get("nombre"), nombre);
    }

    private Specification<UnidadesMedidaEntity> filterByEnabled(String enabled){
        return (root,query, cb) -> cb.equal(root.<Boolean>get("enabled"), Boolean.parseBoolean(enabled));
    }

    private Specification<UnidadesMedidaEntity> filterByDeleted(String deleted){
        return (root,query, cb) -> cb.equal(root.<Boolean>get("deleted"), Boolean.parseBoolean(deleted));
    }


}
