package com.main.tancol.logistica.repositories;

import com.main.tancol.logistica.entities.rutas.VehiculosEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VehiculosRepository extends CrudRepository<VehiculosEntity, UUID>, JpaSpecificationExecutor<VehiculosEntity>{
    @Override
    @EntityGraph(attributePaths = "unidadesMedidaEntity")
    Page<VehiculosEntity> findAll(Specification<VehiculosEntity> spec, Pageable pageable);
}
