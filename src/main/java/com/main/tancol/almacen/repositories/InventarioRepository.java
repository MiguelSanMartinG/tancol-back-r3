package com.main.tancol.almacen.repositories;

import com.main.tancol.almacen.entities.InventarioEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.*;

@Repository
public interface InventarioRepository extends CrudRepository<InventarioEntity, UUID>, JpaSpecificationExecutor<InventarioEntity>{

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<InventarioEntity> findByAlmacen_IdAndProductoBasicoEntity_Id(UUID idAlmacen, UUID idProducto);

    @Query("""
            SELECT i.productoBasicoEntity.id, SUM(i.cantidad)
            FROM InventarioEntity i
            WHERE i.deleted = false
              AND i.productoBasicoEntity.id IN :productoIds
            GROUP BY i.productoBasicoEntity.id
            """)
    List<Object[]> sumCantidadByProductoIds(@Param("productoIds") Collection<UUID> productoIds);

}
