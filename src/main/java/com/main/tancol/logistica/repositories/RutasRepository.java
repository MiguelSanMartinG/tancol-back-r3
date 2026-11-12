package com.main.tancol.logistica.repositories;

import com.main.tancol.logistica.entities.rutas.RutasEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RutasRepository extends CrudRepository<RutasEntity, UUID>, JpaSpecificationExecutor<RutasEntity>{
    // Agrega m??todos personalizados aqu??
}
