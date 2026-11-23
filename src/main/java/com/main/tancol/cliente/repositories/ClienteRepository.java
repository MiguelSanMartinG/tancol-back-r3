package com.main.tancol.cliente.repositories;

import com.main.tancol.cliente.entities.ClienteEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.UUID;

@Repository
public interface ClienteRepository extends CrudRepository<ClienteEntity, UUID>, JpaSpecificationExecutor<ClienteEntity>{
    ClienteEntity getById(UUID id);

    Set<ClienteEntity> findByClienteProductoRel_Cliente_Id(UUID id);

    /** Otro cliente activo con el mismo nombre comercial (sin distinguir mayúsculas ni espacios en los extremos). */
    @Query("""
            SELECT COUNT(c) > 0 FROM ClienteEntity c
            WHERE c.deleted = false AND LOWER(TRIM(c.nombreComercial)) = LOWER(TRIM(:nombre))
            """)
    boolean existeNombreComercial(@Param("nombre") String nombre);

    @Query("""
            SELECT COUNT(c) > 0 FROM ClienteEntity c
            WHERE c.deleted = false AND LOWER(TRIM(c.nombreComercial)) = LOWER(TRIM(:nombre)) AND c.id <> :id
            """)
    boolean existeNombreComercialEnOtro(@Param("nombre") String nombre, @Param("id") UUID id);

    @Override
    @EntityGraph(attributePaths = {"tipoDeCliente", "rutaAsociada"})
    Page<ClienteEntity> findAll(Specification<ClienteEntity> spec, Pageable pageable);
}
