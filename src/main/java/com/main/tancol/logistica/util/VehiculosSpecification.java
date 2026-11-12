package com.main.tancol.logistica.util;


import com.main.tancol.common.exeptions.FilterNotFound;
import com.main.tancol.common.exeptions.IDNotFound;
import com.main.tancol.logistica.entities.rutas.VehiculosEntity;
import com.main.tancol.logistica.enums.EstatusVehiculo;
import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;



public class VehiculosSpecification implements Specification<VehiculosEntity> {

    @SuppressWarnings("null")
    @Nullable
    @Override
    public Predicate toPredicate(Root<VehiculosEntity> root , @Nullable CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder){
        return null;
    }

     public Specification<VehiculosEntity> getSpecificationByFilters(Map<String,String> params){
        Specification<VehiculosEntity> specification = Specification.where(this.filterByDeleted("false"));

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
                case "status":
                    specification = specification.and(this.filterByEstatus(entry.getValue()));
                    break;
                default:
                    throw new FilterNotFound("Invalid filter: " + entry.getKey());

            }
        }
        return specification;
    }

    // Versión corregida
    private Specification<VehiculosEntity> filterByEstatus(String estatus) {
        EstatusVehiculo est;
        try {
            est = EstatusVehiculo.valueOf(estatus.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IDNotFound("Invalid filter: " + estatus);
        }

        // Corrección principal: comparar el campo de la entidad con el enum
        return (root, query, cb) -> cb.equal(root.get("estatusVehiculo"), est);
    }

    private Specification<VehiculosEntity> filterById(String id){
        return (root,query, cb) -> cb.equal(root.<UUID>get("id"), UUID.fromString(id));
    }

    private Specification<VehiculosEntity> filterByName(String nombre){
        return (root,query, cb) -> cb.like(root.get("nombre"), nombre);
    }

    private Specification<VehiculosEntity> filterByEnabled(String enabled){
        return (root,query, cb) -> cb.equal(root.<Boolean>get("enabled"), Boolean.parseBoolean(enabled));
    }

    private Specification<VehiculosEntity> filterByDeleted(String deleted){
        return (root,query, cb) -> cb.equal(root.<Boolean>get("deleted"), Boolean.parseBoolean(deleted));
    }


}
