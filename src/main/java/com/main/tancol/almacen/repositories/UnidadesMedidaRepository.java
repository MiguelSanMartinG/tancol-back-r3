package com.main.tancol.almacen.repositories;

import com.main.tancol.almacen.entities.UnidadesMedidaEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UnidadesMedidaRepository extends CrudRepository<UnidadesMedidaEntity, UUID>, JpaSpecificationExecutor<UnidadesMedidaEntity>{
    UnidadesMedidaEntity findFirstByAlias(String alias);


    // Agrega m??todos personalizados aqu??
}
