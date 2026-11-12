package com.main.tancol.almacen.repositories;

import com.main.tancol.almacen.entities.AlmacenEntity;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AlmacenRepository extends CrudRepository<AlmacenEntity, UUID>, JpaSpecificationExecutor<AlmacenEntity> {
    List<AlmacenEntity> findByNombreContaining(String keyword);

    Optional<AlmacenEntity> findByNombre(@NotNull String nombre);
}