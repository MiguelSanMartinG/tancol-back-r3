package com.main.tancol.producto.util.insumos;

import com.main.tancol.common.exeptions.FilterNotFound;
import com.main.tancol.common.exceptions.exeption.GenericExeption;
import com.main.tancol.producto.SubcategoriaProductoBasico;
import com.main.tancol.producto.entities.insumos.CategoriaProductoEntity;
import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.*;
import java.util.Map.Entry;

public class CateogriaSpecification implements Specification<CategoriaProductoEntity> {

    @SuppressWarnings("null")
    @Nullable
    @Override
    public Predicate toPredicate(Root<CategoriaProductoEntity> root , @Nullable CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder){
        return null;
    }

    public Specification<CategoriaProductoEntity> getSpecificationByFilters(Map<String,String> params){
        Specification<CategoriaProductoEntity> specification = Specification.where(this.filterByDeleted("false"));

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
                case "categoriaPadreId":
                    if (!SubcategoriaProductoBasico.habilitada()) {
                        throw new GenericExeption("Las subcategorías de producto básico están deshabilitadas");
                    }
                    specification = specification.and(this.filterByCategoriaPadre(entry.getValue()));
                    break;

                default:
                    throw new FilterNotFound("Invalid filter: " + entry.getKey());

            }

        }
        return specification;
    }

    private Specification<CategoriaProductoEntity> filterById(String id){
        return (root,query, cb) -> cb.equal(root.<UUID>get("id"), UUID.fromString(id));
    }

    private Specification<CategoriaProductoEntity> filterByName(String nombre){
        return (root,query, cb) -> cb.like(root.get("nombre"), nombre);
    }

    private Specification<CategoriaProductoEntity> filterByEnabled(String enabled){
        return (root,query, cb) -> cb.equal(root.<Boolean>get("enabled"), Boolean.parseBoolean(enabled));
    }

    private Specification<CategoriaProductoEntity> filterByDeleted(String deleted){
        return (root,query, cb) -> cb.equal(root.<Boolean>get("deleted"), Boolean.parseBoolean(deleted));
    }

    private Specification<CategoriaProductoEntity> filterByCategoriaPadre(String categoriaPadreId){
        return (root, query, cb) -> cb.equal(root.get("categoriaPadre").get("id"), UUID.fromString(categoriaPadreId));
    }

}
