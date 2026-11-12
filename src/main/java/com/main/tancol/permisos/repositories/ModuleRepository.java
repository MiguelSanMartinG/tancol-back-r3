package com.main.tancol.permisos.repositories;

import com.main.tancol.permisos.entities.ModuleEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

//@Repository
public interface ModuleRepository extends CrudRepository<ModuleEntity, UUID>, JpaSpecificationExecutor<ModuleEntity>{
    ModuleEntity findByName(String rutas);
    // Agrega m??todos personalizados aqu??
}
