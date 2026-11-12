package com.main.tancol.proveedor.util;

import com.main.tancol.common.exceptions.exeption.FilterNotFound;
import com.main.tancol.proveedor.entity.InformacionFiscal;
import com.main.tancol.proveedor.entity.ProveedorEntity;
import com.main.tancol.proveedor.entity.TipoProveedorEntity;
import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.Map;
import java.util.UUID;

public class ProveedorSpecification implements Specification<ProveedorEntity>{
    @Nullable
    @Override
    public Predicate toPredicate(Root<ProveedorEntity> root , @Nullable CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder){
        return null;
    }

    public Specification<ProveedorEntity> getSpecificationByFilters(Map<String,String> params){
        Specification<ProveedorEntity> specification = Specification.where(this.filterByDeleted("false"));

        if (params == null || params.isEmpty()) {
            return specification;
        }

        for(Map.Entry<String, String> entry : params.entrySet()){
            switch (entry.getKey()){
                case "nombreComercial":
                    specification = specification.and(this.filterByNombreComercial(entry.getValue()));
                    break;
                case "rfc":
                    specification = specification.and(this.filterByRFC(entry.getValue()));
                    break;
                case "idTipoProveedor":
                    specification = specification.and(this.filterByTipoProveedor(entry.getValue()));
                    break;
                case "razonSocial":
                    specification = specification.and(this.filterByRazonSocial(entry.getValue()));
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

    private Specification<ProveedorEntity> filterById(String id){
        return (root,query, cb) -> cb.equal(root.<UUID>get("id"), UUID.fromString(id));
    }

    private Specification<ProveedorEntity> filterByRFC(String rfc) {
        return (root, query, cb) -> {
            Join<ProveedorEntity, InformacionFiscal> join = root.join("informacionFiscal");
            return cb.like(join.get("rfc"), rfc);
        };
    }

    private Specification<ProveedorEntity> filterByNombreComercial(String nombreComercial){
        return (root,query, cb) -> cb.like(root.get("nombreComercial"), nombreComercial);
    }
    private Specification<ProveedorEntity> filterByRazonSocial(String razonSocial){
        return (root,query, cb) -> cb.like(root.get("razonSocial"), razonSocial);
    }

    private Specification<ProveedorEntity> filterByEnabled(String enabled){
        return (root,query, cb) -> cb.equal(root.<Boolean>get("enabled"), Boolean.parseBoolean(enabled));
    }

    private Specification<ProveedorEntity> filterByTipoProveedor(String idTipoProveedor){
        return (root, query, cb) -> {
            Join<ProveedorEntity, TipoProveedorEntity> tipo = root.join("tiposProveedor");
            if (query != null) {
                query.distinct(true);
            }
            return cb.equal(tipo.get("id"), UUID.fromString(idTipoProveedor));
        };
    }

    private Specification<ProveedorEntity> filterByDeleted(String deleted){
        return (root,query, cb) -> cb.equal(root.<Boolean>get("deleted"), Boolean.parseBoolean(deleted));
    }
}
