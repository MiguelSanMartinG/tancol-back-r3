package com.main.tancol.logistica.util.ruta;


import com.main.tancol.common.exeptions.FilterNotFound;
import com.main.tancol.logistica.entities.rutas.RutasEntity;
import com.main.tancol.logistica.enums.TipoRuta;
import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;



public class RutasSpecification implements Specification<RutasEntity> {

    @SuppressWarnings("null")
    @Nullable
    @Override
    public Predicate toPredicate(Root<RutasEntity> root , @Nullable CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder){
        return null;
    }

     public Specification<RutasEntity> getSpecificationByFilters(Map<String,String> params){
        Specification<RutasEntity> specification = Specification.where(this.filterByDeleted("false"));

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
                case "tipoRuta":
                    specification = specification.and(this.filterByTipoRuta(entry.getValue()));
                    break;

                default:
                    throw new FilterNotFound("Invalid filter: " + entry.getKey());

            }
        }
        return specification;
    }

    private Specification<RutasEntity> filterById(String id){
        return (root,query, cb) -> cb.equal(root.<UUID>get("id"), UUID.fromString(id));
    }

    private Specification<RutasEntity> filterByName(String nombre){
        return (root,query, cb) -> cb.like(root.get("nombre"), nombre);
    }

    private Specification<RutasEntity> filterByEnabled(String enabled){
        return (root,query, cb) -> cb.equal(root.<Boolean>get("enabled"), Boolean.parseBoolean(enabled));
    }

    private Specification<RutasEntity> filterByTipoRuta(String tipoRuta){
        final TipoRuta tipo;
        try {
            tipo = TipoRuta.valueOf(tipoRuta);
        } catch (IllegalArgumentException ex) {
            throw new FilterNotFound("Invalid tipoRuta value: " + tipoRuta);
        }
        return (root, query, cb) -> cb.equal(root.get("tipoRuta"), tipo);
    }

    private Specification<RutasEntity> filterByDeleted(String deleted){
        return (root,query, cb) -> cb.equal(root.<Boolean>get("deleted"), Boolean.parseBoolean(deleted));
    }


}
