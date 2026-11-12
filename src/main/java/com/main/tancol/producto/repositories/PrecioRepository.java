package com.main.tancol.producto.repositories;

import com.main.tancol.producto.entities.PresentacionProductoBasico;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface PrecioRepository extends CrudRepository<PresentacionProductoBasico, UUID> {

//    Optional<ProductosProveedoresEntity> findByProducto_IdAndProveedor_Id(UUID product, UUID proveedor);

//    Optional<PrecioEntity> findByProveedor_IdAndProductoEntity_Id(UUID product, UUID proveedor);

    Optional<PresentacionProductoBasico> findFirstByProductoBasicoEntity_IdAndProveedorEntity_Id(UUID productoId, UUID proveedorId);

    Optional<PresentacionProductoBasico> findByProductoBasicoEntity_IdAndProveedorEntity_Id(UUID id, UUID id1);

    boolean existsByProductoBasicoEntity_IdAndProveedorEntity_Id(UUID id, UUID id1);

}