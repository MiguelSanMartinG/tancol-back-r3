package com.main.tancol.tablascomisiones.repositories;

import com.main.tancol.producto.entities.comerciales.ProductoComercialEntity;
import com.main.tancol.tablascomisiones.entities.TablasComisionesEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface TablasComisionesRepository extends CrudRepository<TablasComisionesEntity, UUID>,
        JpaSpecificationExecutor<TablasComisionesEntity> {

    boolean existsByProducto_IdAndDeletedFalse(UUID productoId);

    boolean existsByProducto_IdAndDeletedFalseAndIdNot(UUID productoId, UUID id);

    @Query("""
            SELECT p FROM ProductoComercialEntity p
            WHERE p.deleted = false
              AND p.enabled = true
              AND NOT EXISTS (
                    SELECT 1 FROM TablasComisionesEntity c
                    WHERE c.producto = p
                      AND c.deleted = false
                      AND (:tablaId IS NULL OR c.id <> :tablaId)
              )
            ORDER BY p.nombre ASC
            """)
    List<ProductoComercialEntity> findProductosDisponibles(@Param("tablaId") UUID tablaId);

    @Query("""
            SELECT c FROM TablasComisionesEntity c
            JOIN FETCH c.producto
            WHERE c.deleted = false
              AND c.producto.id IN :productoIds
            """)
    List<TablasComisionesEntity> findByProductoIdInAndDeletedFalse(@Param("productoIds") Collection<UUID> productoIds);
}
