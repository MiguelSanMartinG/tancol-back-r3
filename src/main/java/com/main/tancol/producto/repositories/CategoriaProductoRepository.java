package com.main.tancol.producto.repositories;

import com.main.tancol.producto.entities.CategoriaProductoEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface CategoriaProductoRepository extends CrudRepository<CategoriaProductoEntity, UUID>, JpaSpecificationExecutor<CategoriaProductoEntity> {
    Optional<CategoriaProductoEntity> findByNombre(String nombre);

}
