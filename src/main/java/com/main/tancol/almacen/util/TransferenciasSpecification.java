package com.main.tancol.almacen.util;


import com.main.tancol.common.exceptions.exeption.FilterNotFound;
import com.main.tancol.almacen.entities.*;
import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.*;
import java.util.Map.Entry;



public class TransferenciasSpecification implements Specification<TransferenciasEntity> {

    @SuppressWarnings("null")
    @Nullable
    @Override
    public Predicate toPredicate(Root<TransferenciasEntity> root , @Nullable CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder){
        return null;
    }

     public Specification<TransferenciasEntity> getSpecificationByFilters(Map<String,String> params){
        Specification<TransferenciasEntity> specification = Specification.where(this.filterByDeleted("false"));

        if (params == null || params.isEmpty()) {
            return specification;
        }

        for(Entry<String, String> entry : params.entrySet()){
            switch (entry.getKey()){
                case "id":
                    specification = specification.and(this.filterById(entry.getValue()));
                    break;
                case "motivo":
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

    private Specification<TransferenciasEntity> filterById(String id){
        return (root,query, cb) -> cb.equal(root.<UUID>get("idTransferencia"), UUID.fromString(id));
    }

    private Specification<TransferenciasEntity> filterByName(String nombre){
        return (root,query, cb) -> cb.like(cb.lower(root.get("motivo")), "%" + nombre.toLowerCase() + "%");
    }

    private Specification<TransferenciasEntity> filterByEnabled(String enabled){
        return (root,query, cb) -> cb.equal(root.<Boolean>get("enabled"), Boolean.parseBoolean(enabled));
    }

    private Specification<TransferenciasEntity> filterByDeleted(String deleted){
        return (root,query, cb) -> cb.equal(root.<Boolean>get("deleted"), Boolean.parseBoolean(deleted));
    }


}
