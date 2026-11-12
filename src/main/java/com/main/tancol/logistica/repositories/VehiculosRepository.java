package com.main.tancol.logistica.repositories;

import com.main.tancol.logistica.entities.rutas.VehiculosEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VehiculosRepository extends CrudRepository<VehiculosEntity, UUID>, JpaSpecificationExecutor<VehiculosEntity>{
    // Agrega m??todos personalizados aqu??
}
