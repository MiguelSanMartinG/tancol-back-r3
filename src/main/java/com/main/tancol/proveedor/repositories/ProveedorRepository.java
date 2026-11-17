package com.main.tancol.proveedor.repositories;

import com.main.tancol.proveedor.entity.ProveedorEntity;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProveedorRepository extends CrudRepository<ProveedorEntity, UUID>, JpaSpecificationExecutor<ProveedorEntity> {
    Optional<ProveedorEntity> findByNombreComercial(@NotNull String nombre);

    // Los tipos (ManyToMany) se cargan por lotes con hibernate.default_batch_fetch_size;
    // un fetch de la colección aquí obligaría a paginar en memoria.
}
