package com.main.tancol.producto.repositories;

import com.main.tancol.producto.entities.PrecioEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface PrecioRepository extends CrudRepository<PrecioEntity, UUID> {

//    Optional<ProductosProveedoresEntity> findByProducto_IdAndProveedor_Id(UUID product, UUID proveedor);

//    Optional<PrecioEntity> findByProveedor_IdAndProductoEntity_Id(UUID product, UUID proveedor);

    Optional<PrecioEntity> findByProductoEntity_IdAndProveedorEntity_Id(UUID productoId, UUID proveedorId);
}