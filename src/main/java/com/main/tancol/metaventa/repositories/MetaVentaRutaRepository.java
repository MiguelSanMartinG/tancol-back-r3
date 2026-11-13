package com.main.tancol.metaventa.repositories;

import com.main.tancol.metaventa.entities.MetaVentaRutaEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MetaVentaRutaRepository extends CrudRepository<MetaVentaRutaEntity, UUID>,
        JpaSpecificationExecutor<MetaVentaRutaEntity> {

    boolean existsByRutaIdAndProductoIdAndActivaTrueAndDeletedFalse(UUID rutaId, UUID productoId);
}
