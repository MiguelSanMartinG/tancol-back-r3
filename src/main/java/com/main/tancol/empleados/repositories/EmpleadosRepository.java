package com.main.tancol.empleados.repositories;

import com.main.tancol.empleados.entities.EmpleadosEntity;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmpleadosRepository extends CrudRepository<EmpleadosEntity, UUID>, JpaSpecificationExecutor<EmpleadosEntity> {

    @Override
    @EntityGraph(attributePaths = {"role", "tiposEmpleados", "departamento"})
    Page<EmpleadosEntity> findAll(Specification<EmpleadosEntity> spec, Pageable pageable);

    Optional<EmpleadosEntity> findByNombreUsuario(String nombreUsuario);

    @QueryHints(@QueryHint(name = "hibernate.query.passDistinctThrough", value = "false"))
    @Query("""
            SELECT DISTINCT e FROM EmpleadosEntity e
            LEFT JOIN FETCH e.role r
            LEFT JOIN FETCH r.roleProfile p
            LEFT JOIN FETCH p.module m
            WHERE e.nombreUsuario = :nombreUsuario
            """)
    List<EmpleadosEntity> findWithPermisosByNombreUsuario(@Param("nombreUsuario") String nombreUsuario);

    List<EmpleadosEntity> findByDeletedFalseAndEnabledTrueAndDepartamento_Id(UUID departamentoId);

    boolean existsByIdEmpleado(Long idEmpleado);

    @Query("""
            SELECT DISTINCT e FROM EmpleadosEntity e
            JOIN e.role r
            JOIN r.roleProfile p
            JOIN p.module m
            WHERE e.deleted = false
              AND e.enabled = true
              AND r.deleted = false
              AND p.deleted = false
              AND m.deleted = false
              AND m.name = :moduleName
              AND p.canRead = true
            """)
    List<EmpleadosEntity> findByModuleReadPermission(@Param("moduleName") String moduleName);

    @org.springframework.data.jpa.repository.Query("""
            SELECT e FROM EmpleadosEntity e
            WHERE e.deleted = false
              AND e.enabled = true
              AND e.tipoContratacion IN :tipos
              AND e.fechaVigencia BETWEEN :desde AND :hasta
            ORDER BY e.fechaVigencia ASC
            """)
    java.util.List<EmpleadosEntity> findConVigenciaEntre(
            @org.springframework.data.repository.query.Param("tipos") java.util.Collection<com.main.tancol.empleados.enums.TipoContratacion> tipos,
            @org.springframework.data.repository.query.Param("desde") java.time.LocalDate desde,
            @org.springframework.data.repository.query.Param("hasta") java.time.LocalDate hasta);
}
