package com.main.tancol.proveedor.repositories;

import com.main.tancol.proveedor.entity.ProveedorEntity;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProveedorRepository extends CrudRepository<ProveedorEntity, UUID>, JpaSpecificationExecutor<ProveedorEntity> {
    Optional<ProveedorEntity> findByNombreComercial(@NotNull String nombre);

    @Override
    @EntityGraph(attributePaths = "tipoProveedorEntity")
    Page<ProveedorEntity> findAll(Specification<ProveedorEntity> spec, Pageable pageable);
}
