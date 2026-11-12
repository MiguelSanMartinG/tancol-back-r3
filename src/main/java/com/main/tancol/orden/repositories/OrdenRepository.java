package com.main.tancol.orden.repositories;

import com.main.tancol.common.interfaces.CheckDeleted;
import com.main.tancol.orden.entities.OrdenEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrdenRepository extends CrudRepository<OrdenEntity, UUID>, JpaSpecificationExecutor<OrdenEntity>, CheckDeleted<OrdenEntity> {
    // Agrega m??todos personalizados aqu??

    @Override
    @EntityGraph(attributePaths = {"proveedor"})
    Page<OrdenEntity> findAll(Specification<OrdenEntity> spec, Pageable pageable);

    List<OrdenEntity> findByFacturacionEstadoNotNull(Sort sort);

    List<OrdenEntity> findByRecepcionEstatusNotNullOrderByUpdatedAtAsc();

}
