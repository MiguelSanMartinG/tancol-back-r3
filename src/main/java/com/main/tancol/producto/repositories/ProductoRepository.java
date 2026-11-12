package com.main.tancol.producto.repositories;

import com.main.tancol.producto.entities.ProductoEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProductoRepository extends CrudRepository<ProductoEntity, UUID>, JpaSpecificationExecutor<ProductoEntity> {
    Optional<ProductoEntity> findByNombre(String nombre);

    boolean existsProductoByNombre(String nombre);
}
