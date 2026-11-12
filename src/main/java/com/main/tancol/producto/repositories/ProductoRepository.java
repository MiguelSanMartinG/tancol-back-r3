package com.main.tancol.producto.repositories;

import com.main.tancol.producto.entities.insumos.ProductoBasicoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductoRepository extends CrudRepository<ProductoBasicoEntity, UUID>, JpaSpecificationExecutor<ProductoBasicoEntity> {
    @Override
    @EntityGraph(attributePaths = {"categoriaProductoEntity", "unidadMedidaDefault"})
    Page<ProductoBasicoEntity> findAll(Specification<ProductoBasicoEntity> spec, Pageable pageable);

    Optional<ProductoBasicoEntity> findByNombre(String nombre);
    boolean existsProductoByNombre(String nombre);

    List<ProductoBasicoEntity> findByDeletedFalseAndEnabledTrueAndNotificarStockMinimoTrueAndStockMinimoIsNotNull();

    List<ProductoBasicoEntity> findByDeletedFalseAndEnabledTrueAndCategoriaProductoEntity_Id(UUID categoriaId);

    List<ProductoBasicoEntity> findByDeletedFalseAndEnabledTrueAndCategoriaProductoEntity_IdIn(Collection<UUID> categoriaIds);

    @Query("""
            SELECT p FROM ProductoBasicoEntity p
            LEFT JOIN FETCH p.categoriaProductoEntity
            WHERE p.deleted = false AND p.enabled = true
            ORDER BY p.nombre
            """)
    List<ProductoBasicoEntity> findActivosOrdenados();
}
