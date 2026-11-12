package com.main.tancol.producto.util.insumos;

import com.main.tancol.common.exceptions.exeption.FilterNotFound;
import com.main.tancol.producto.entities.insumos.ProductoBasicoEntity;
import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.Map;
import java.util.UUID;

public class ProductoSpecification implements Specification<ProductoBasicoEntity> {

    @SuppressWarnings("null")
    @Nullable
    @Override
    public Predicate toPredicate(Root<ProductoBasicoEntity> root , @Nullable CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder){
        return null;
    }

    public Specification<ProductoBasicoEntity> getSpecificationByFilters(Map<String,String> params){
        Specification<ProductoBasicoEntity> specification = Specification.where(this.filterByDeleted("false"));

        if (params == null || params.isEmpty()) {
            return specification;
        }

        for(Map.Entry<String, String> entry : params.entrySet()){
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
                case "sku":
                    specification = specification.and(this.filterBySku(entry.getValue()));
                    break;
                case "alias":
                    specification = specification.and(this.filterByAlias(entry.getValue()));
                    break;

                default:
                    throw new FilterNotFound("Invalid filter: " + entry.getKey());

            }

        }
        return specification;
    }

    private Specification<ProductoBasicoEntity> filterByAlias(String value) {
        return (root,query, cb) -> cb.like(root.get("alias"), value);

    }

    private Specification<ProductoBasicoEntity> filterBySku(String value) {
        return (root,query, cb) -> cb.like(root.get("sku"), value);

    }

    private Specification<ProductoBasicoEntity> filterById(String id){
        return (root,query, cb) -> cb.equal(root.<UUID>get("id"), UUID.fromString(id));
    }

    private Specification<ProductoBasicoEntity> filterByName(String nombre){
        return (root,query, cb) -> cb.like(root.get("nombre"), nombre);
    }

    private Specification<ProductoBasicoEntity> filterByEnabled(String enabled){
        return (root,query, cb) -> cb.equal(root.<Boolean>get("enabled"), Boolean.parseBoolean(enabled));
    }

    private Specification<ProductoBasicoEntity> filterByDeleted(String deleted){
        return (root,query, cb) -> cb.equal(root.<Boolean>get("deleted"), Boolean.parseBoolean(deleted));
    }

}
