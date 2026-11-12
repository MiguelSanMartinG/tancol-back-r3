package com.main.tancol.cliente.util;


import com.main.tancol.cliente.entities.ClienteEntity;
import com.main.tancol.cliente.entities.TipoClienteEntity;
import com.main.tancol.common.exeptions.FilterNotFound;
import com.main.tancol.proveedor.enums.TipoPago;
import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;



public class ClienteSpecification implements Specification<ClienteEntity> {

    @SuppressWarnings("null")
    @Nullable
    @Override
    public Predicate toPredicate(Root<ClienteEntity> root , @Nullable CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder){
        return null;
    }

     public Specification<ClienteEntity> getSpecificationByFilters(Map<String,String> params){
        Specification<ClienteEntity> specification = Specification.where(this.filterByDeleted("false"));

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
                case "noCliente":
                    specification = specification.and(this.filterByNoCliente(entry.getValue()));
                    break;
                case "tipoDeCliente":
                    specification = specification.and(this.filterByTipoCliente(entry.getValue()));
                    break;
                case "nombreComercial":
                    specification = specification.and(this.fiterByNombreComercial(entry.getValue()));
                    break;
                case "tipoPago":
                    specification = specification.and(this.fiterByTipoPago(entry.getValue()));
                    break;
                case "fechaAlta":
                    specification = specification.and(this.filterByFechaAlta(LocalDateTime.parse(entry.getValue())));
                    break;
                case "tipoContribuyente":
                    specification = specification.and(this.fiterByTipoContribuyente(entry.getValue()));
                    break;

                default:
                    throw new FilterNotFound("Invalid filter: " + entry.getKey());

            }
        }
        return specification;
    }

    private Specification<ClienteEntity> fiterByTipoContribuyente(String value) {
        return (root,query, cb) -> {
            Join<ClienteEntity, TipoPago> tipoCliente = root.join("tipoContribuyente");
            return cb.like(tipoCliente.get("nombre"), value);
        };
    }

    private Specification<ClienteEntity> fiterByTipoPago(String value) {
        return (root,query, cb) -> cb.like(root.get("tipoPago"), value);
    }

    private Specification<ClienteEntity> fiterByNombreComercial(String value) {
        return (root,query, cb) -> cb.like(root.get("nombreComercial"), value);
    }

    private Specification<ClienteEntity> filterByTipoCliente(String value) {
        return (root,query, cb) -> {
            Join<ClienteEntity, TipoClienteEntity> tipoCliente = root.join("tipoCliente");
            return cb.like(tipoCliente.get("nombre"), value);
        };
    }

    private Specification<ClienteEntity> filterByNoCliente(String value) {
        return (root,query, cb) -> cb.equal(root.<Long>get("noCliente"), Long.parseLong(value));
    }

    private Specification<ClienteEntity> filterById(String id){
        return (root,query, cb) -> cb.equal(root.<UUID>get("id"), UUID.fromString(id));
    }

    private Specification<ClienteEntity> filterByFechaAlta(LocalDateTime fechaAlta){
        return (root,query, cb) -> cb.equal(root.get("fechaAlta"), fechaAlta);
    }

    private Specification<ClienteEntity> filterByName(String nombre){
        return (root,query, cb) -> cb.like(root.get("nombre"), nombre);
    }

    private Specification<ClienteEntity> filterByEnabled(String enabled){
        return (root,query, cb) -> cb.equal(root.<Boolean>get("enabled"), Boolean.parseBoolean(enabled));
    }

    private Specification<ClienteEntity> filterByDeleted(String deleted){
        return (root,query, cb) -> cb.equal(root.<Boolean>get("deleted"), Boolean.parseBoolean(deleted));
    }



}
