package com.main.tancol.orden.repositories;

import com.main.tancol.common.interfaces.CheckDeleted;
import com.main.tancol.orden.entities.OrdenEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrdenRepository extends CrudRepository<OrdenEntity, UUID>, JpaSpecificationExecutor<OrdenEntity>, CheckDeleted<OrdenEntity> {
    // Agrega m??todos personalizados aqu??

    List<OrdenEntity> findByPostAutorizadoEstatusNotNull();

    List<OrdenEntity> findByRecepcionEstatusNotNull();
}
