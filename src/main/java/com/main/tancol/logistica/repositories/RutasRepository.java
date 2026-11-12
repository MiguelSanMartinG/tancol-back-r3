package com.main.tancol.logistica.repositories;

import com.main.tancol.logistica.entities.rutas.RutasEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RutasRepository extends CrudRepository<RutasEntity, UUID>, JpaSpecificationExecutor<RutasEntity>{
    @Override
    @EntityGraph(attributePaths = {"chofer", "productBase", "vehiculo"})
    Page<RutasEntity> findAll(Specification<RutasEntity> spec, Pageable pageable);
}
