package com.main.tancol.logistica.util;


import com.main.tancol.common.exeptions.FilterNotFound;
import com.main.tancol.logistica.entities.bitacora.comisiones.VentasEmpleadoEntity;
import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;



public class VentasEmpleadoSpecification implements Specification<VentasEmpleadoEntity> {

    @SuppressWarnings("null")
    @Nullable
    @Override
    public Predicate toPredicate(Root<VentasEmpleadoEntity> root , @Nullable CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder){
        return null;
    }

     public Specification<VentasEmpleadoEntity> getSpecificationByFilters(Map<String,String> params){
        Specification<VentasEmpleadoEntity> specification = Specification.where(this.filterByDeleted("false"));

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

    private Specification<VentasEmpleadoEntity> filterById(String id){
        return (root,query, cb) -> cb.equal(root.<UUID>get("id"), UUID.fromString(id));
    }

    private Specification<VentasEmpleadoEntity> filterByName(String nombre){
        return (root,query, cb) -> cb.like(root.get("nombre"), nombre);
    }

    private Specification<VentasEmpleadoEntity> filterByEnabled(String enabled){
        return (root,query, cb) -> cb.equal(root.<Boolean>get("enabled"), Boolean.parseBoolean(enabled));
    }

    private Specification<VentasEmpleadoEntity> filterByDeleted(String deleted){
        return (root,query, cb) -> cb.equal(root.<Boolean>get("deleted"), Boolean.parseBoolean(deleted));
    }


}
