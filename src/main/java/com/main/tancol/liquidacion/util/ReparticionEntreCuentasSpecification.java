package com.main.tancol.liquidacion.util;

import com.main.tancol.common.exceptions.exeption.FilterNotFound;
import com.main.tancol.liquidacion.entities.ReparticionEntreCuentasEntity;
import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;



public class ReparticionEntreCuentasSpecification implements Specification<ReparticionEntreCuentasEntity> {

    @SuppressWarnings("null")
    @Nullable
    @Override
    public Predicate toPredicate(Root<ReparticionEntreCuentasEntity> root , @Nullable CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder){
        return null;
    }

     public Specification<ReparticionEntreCuentasEntity> getSpecificationByFilters(Map<String,String> params){
        Specification<ReparticionEntreCuentasEntity> specification = Specification.where(this.filterByDeleted("false"));

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

    private Specification<ReparticionEntreCuentasEntity> filterById(String id){
        return (root,query, cb) -> cb.equal(root.<UUID>get("id"), UUID.fromString(id));
    }

    private Specification<ReparticionEntreCuentasEntity> filterByName(String nombre){
        return (root,query, cb) -> cb.like(root.get("nombre"), nombre);
    }

    private Specification<ReparticionEntreCuentasEntity> filterByEnabled(String enabled){
        return (root,query, cb) -> cb.equal(root.<Boolean>get("enabled"), Boolean.parseBoolean(enabled));
    }

    private Specification<ReparticionEntreCuentasEntity> filterByDeleted(String deleted){
        return (root,query, cb) -> cb.equal(root.<Boolean>get("deleted"), Boolean.parseBoolean(deleted));
    }

//    private Specification<ReparticionEntreCuentasEntity> filterByIdToken(String value) {
//        return (root,query, cb) -> {
//            UUID id = UUID.fromString(value);
//        Join<ReparticionEntreCuentasEntity, HijoEntity> join = root.join("campoDelEntityPadre");
//            return cb.equal(join.get("campoDelHijo"), id); };
//    }



}
