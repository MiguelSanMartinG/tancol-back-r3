package com.main.tancol.prestamo.repositories;

import com.main.tancol.prestamo.entities.TipoEquipoEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TipoEquipoRepository extends CrudRepository<TipoEquipoEntity, UUID>, JpaSpecificationExecutor<TipoEquipoEntity> {
}
