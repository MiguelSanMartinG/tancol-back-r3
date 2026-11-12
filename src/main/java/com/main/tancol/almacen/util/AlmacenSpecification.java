package com.main.tancol.almacen.util;

import com.main.tancol.auth.exceptions.exeption.FilterNotFound;
import com.main.tancol.almacen.entities.Almacen;
import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;

public class AlmacenSpecification implements Specification<Almacen> {

    @SuppressWarnings("null")
    @Nullable
    @Override
    public Predicate toPredicate(Root<Almacen> root , @Nullable CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder){
        return null;
    }

    public Specification<Almacen> getSpecificationByFilters(Map<String,String> params){
        Specification<Almacen> specification = Specification.where(this.filterByDeleted("false"));

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
                default:
                    throw new FilterNotFound("Invalid filter: " + entry.getKey());

            }

        }
        return specification;
    }

    private Specification<Almacen> filterById(String id){
        return (root,query, cb) -> cb.equal(root.<UUID>get("id"), UUID.fromString(id));
    }

    private Specification<Almacen> filterByName(String nombre){
        return (root,query, cb) -> cb.like(root.get("nombre"), nombre);
    }

    private Specification<Almacen> filterByEnabled(String enabled){
        return (root,query, cb) -> cb.equal(root.<Boolean>get("enabled"), Boolean.parseBoolean(enabled));
    }

    private Specification<Almacen> filterByDeleted(String deleted){
        return (root,query, cb) -> cb.equal(root.<Boolean>get("deleted"), Boolean.parseBoolean(deleted));
    }

}
