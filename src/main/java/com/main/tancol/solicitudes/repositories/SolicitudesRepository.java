package com.main.tancol.solicitudes.repositories;

import com.main.tancol.solicitudes.entities.SolicitudEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SolicitudesRepository extends CrudRepository<SolicitudEntity, UUID>, JpaSpecificationExecutor<SolicitudEntity>{
    // Agrega m??todos personalizados aqu??
}
