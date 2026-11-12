package com.main.tancol.almacen.repositories;

import com.main.tancol.almacen.entities.WhitelistProductoEmpleadoEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WhitelistProductoEmpleadoRepository extends CrudRepository<WhitelistProductoEmpleadoEntity, UUID>, JpaSpecificationExecutor<WhitelistProductoEmpleadoEntity> {

    Optional<WhitelistProductoEmpleadoEntity> findFirstByEmpleado_IdAndProducto_IdAndDeletedFalse(UUID empleadoId, UUID productoId);

    List<WhitelistProductoEmpleadoEntity> findByEmpleado_IdAndDeletedFalse(UUID empleadoId);

    @Query("""
            SELECT w FROM WhitelistProductoEmpleadoEntity w
            JOIN FETCH w.producto p
            LEFT JOIN FETCH p.categoriaProductoEntity
            WHERE w.empleado.id = :empleadoId
              AND w.deleted = false
              AND w.enabled = true
              AND p.deleted = false
              AND p.enabled = true
            ORDER BY p.nombre
            """)
    List<WhitelistProductoEmpleadoEntity> findActivasConProducto(@Param("empleadoId") UUID empleadoId);
}
