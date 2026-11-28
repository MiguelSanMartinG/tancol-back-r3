package com.main.tancol.inventariodiario.repositories;

import com.main.tancol.almacen.entities.InventarioEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Existencias del almacén (tabla inventarios) que usa el inventario diario. */
@Repository
public interface InvDiarioInventarioRepository extends JpaRepository<InventarioEntity, UUID> {

    @Query("SELECT i FROM InventarioEntity i WHERE i.almacen.id = :almacenId AND i.productoBasicoEntity.id = :insumoId AND i.deleted = false")
    Optional<InventarioEntity> existencia(@Param("almacenId") UUID almacenId, @Param("insumoId") UUID insumoId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM InventarioEntity i WHERE i.almacen.id = :almacenId AND i.productoBasicoEntity.id = :insumoId AND i.deleted = false")
    Optional<InventarioEntity> bloquear(@Param("almacenId") UUID almacenId, @Param("insumoId") UUID insumoId);

    @Query("SELECT i FROM InventarioEntity i JOIN FETCH i.productoBasicoEntity p WHERE i.almacen.id = :almacenId AND i.deleted = false AND p.deleted = false ORDER BY p.nombre")
    List<InventarioEntity> delAlmacen(@Param("almacenId") UUID almacenId);

    /** [almacenId, insumos con registro] de todos los almacenes (para «400 insumos» en el selector). */
    @Query("SELECT i.almacen.id, COUNT(DISTINCT p.id) FROM InventarioEntity i JOIN i.productoBasicoEntity p WHERE i.deleted = false AND p.deleted = false GROUP BY i.almacen.id")
    List<Object[]> totalesPorAlmacen();
}
