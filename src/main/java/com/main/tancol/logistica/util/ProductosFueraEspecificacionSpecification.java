package com.main.tancol.logistica.util;


import com.main.tancol.common.exeptions.FilterNotFound;
import com.main.tancol.logistica.entities.bitacora.comisiones.ProductosFueraEspecificacionEntity;
import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;



public class ProductosFueraEspecificacionSpecification implements Specification<ProductosFueraEspecificacionEntity> {

    @SuppressWarnings("null")
    @Nullable
    @Override
    public Predicate toPredicate(Root<ProductosFueraEspecificacionEntity> root , @Nullable CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder){
        return null;
    }

     public Specification<ProductosFueraEspecificacionEntity> getSpecificationByFilters(Map<String,String> params){
        Specification<ProductosFueraEspecificacionEntity> specification = Specification.where(this.filterByDeleted("false"));

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

    private Specification<ProductosFueraEspecificacionEntity> filterById(String id){
        return (root,query, cb) -> cb.equal(root.<UUID>get("id"), UUID.fromString(id));
    }

    private Specification<ProductosFueraEspecificacionEntity> filterByName(String nombre){
        return (root,query, cb) -> cb.like(root.get("nombre"), nombre);
    }

    private Specification<ProductosFueraEspecificacionEntity> filterByEnabled(String enabled){
        return (root,query, cb) -> cb.equal(root.<Boolean>get("enabled"), Boolean.parseBoolean(enabled));
    }

    private Specification<ProductosFueraEspecificacionEntity> filterByDeleted(String deleted){
        return (root,query, cb) -> cb.equal(root.<Boolean>get("deleted"), Boolean.parseBoolean(deleted));
    }

//    private Specification<ProductosFueraEspecificacionEntity> filterByIdToken(String value) {
//        return (root,query, cb) -> {
//            UUID id = UUID.fromString(value);
//        Join<ProductosFueraEspecificacionEntity, HijoEntity> join = root.join("campoDelEntityPadre");
//            return cb.equal(join.get("campoDelHijo"), id); };
//    }



}
