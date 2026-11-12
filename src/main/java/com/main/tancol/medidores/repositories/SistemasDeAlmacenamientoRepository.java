package com.main.tancol.medidores.repositories;

import com.main.tancol.medidores.entities.SistemasDeAlmacenamientoEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SistemasDeAlmacenamientoRepository extends CrudRepository<SistemasDeAlmacenamientoEntity, UUID>, JpaSpecificationExecutor<SistemasDeAlmacenamientoEntity>{
    // Agrega m??todos personalizados aqu??
}
