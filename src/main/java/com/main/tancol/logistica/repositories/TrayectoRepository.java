package com.main.tancol.logistica.repositories;


import com.main.tancol.logistica.entities.bitacora.TrayectoEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TrayectoRepository extends CrudRepository<TrayectoEntity, UUID>, JpaSpecificationExecutor<TrayectoEntity>{
    // Agrega m??todos personalizados aqu??
}
