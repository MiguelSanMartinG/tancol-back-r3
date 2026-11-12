package com.main.tancol.tablasamonestaciones.repositories;

import com.main.tancol.producto.entities.comerciales.ProductoComercialEntity;
import com.main.tancol.tablasamonestaciones.entities.TablasAmonestacionesEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TablasAmonestacionesRepository extends CrudRepository<TablasAmonestacionesEntity, UUID>,
        JpaSpecificationExecutor<TablasAmonestacionesEntity> {

    boolean existsByProducto_IdAndDeletedFalse(UUID productoId);

    boolean existsByProducto_IdAndDeletedFalseAndIdNot(UUID productoId, UUID id);

    @Query("""
            SELECT p FROM ProductoComercialEntity p
            WHERE p.deleted = false
              AND p.enabled = true
              AND NOT EXISTS (
                    SELECT 1 FROM TablasAmonestacionesEntity a
                    WHERE a.producto = p
                      AND a.deleted = false
                      AND (:tablaId IS NULL OR a.id <> :tablaId)
              )
            ORDER BY p.nombre ASC
            """)
    List<ProductoComercialEntity> findProductosDisponibles(@Param("tablaId") UUID tablaId);
}
