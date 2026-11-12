package com.main.tancol.medidores.util;


import com.main.tancol.common.exeptions.FilterNotFound;
import com.main.tancol.medidores.entities.BitacoraMedicionEntity;
import com.main.tancol.medidores.entities.MedidoresEntity;
import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;



public class BitacoraMedicionSpecification implements Specification<BitacoraMedicionEntity> {

    @SuppressWarnings("null")
    @Nullable
    @Override
    public Predicate toPredicate(Root<BitacoraMedicionEntity> root , @Nullable CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder){
        return null;
    }

     public Specification<BitacoraMedicionEntity> getSpecificationByFilters(Map<String,String> params){
        Specification<BitacoraMedicionEntity> specification = Specification.where(null);

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
                case "idMedidor":
                    specification = specification.and(this.filterByIdMedidor(entry.getValue()));
                    break;
                default:
                    throw new FilterNotFound("Invalid filter: " + entry.getKey());

            }
        }
        return specification;
    }

    private Specification<BitacoraMedicionEntity> filterByIdMedidor(String value) {

        return (root,query, cb) ->{
            UUID idMedidor = UUID.fromString(value);
        Join<BitacoraMedicionEntity, MedidoresEntity> join = root.join("medidor");
            return cb.equal(join.get("idMedidor"), idMedidor);
        };
    }

    private Specification<BitacoraMedicionEntity> filterById(String id){
        return (root,query, cb) -> cb.equal(root.<UUID>get("id"), UUID.fromString(id));
    }

    private Specification<BitacoraMedicionEntity> filterByName(String nombre){
        return (root,query, cb) -> cb.like(root.get("nombre"), nombre);
    }

    private Specification<BitacoraMedicionEntity> filterByEnabled(String enabled){
        return (root,query, cb) -> cb.equal(root.<Boolean>get("enabled"), Boolean.parseBoolean(enabled));
    }

    private Specification<BitacoraMedicionEntity> filterByDeleted(String deleted){
        return (root,query, cb) -> cb.equal(root.<Boolean>get("deleted"), Boolean.parseBoolean(deleted));
    }


}
