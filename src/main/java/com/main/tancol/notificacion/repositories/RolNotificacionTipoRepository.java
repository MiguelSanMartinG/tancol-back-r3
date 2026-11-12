package com.main.tancol.notificacion.repositories;

import com.main.tancol.empleados.entities.EmpleadosEntity;
import com.main.tancol.notificacion.entities.RolNotificacionTipoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RolNotificacionTipoRepository extends JpaRepository<RolNotificacionTipoEntity, UUID> {

    @Query("""
            SELECT a FROM RolNotificacionTipoEntity a
            JOIN FETCH a.tipo t
            WHERE a.role.id = :roleId
              AND a.deleted = false
              AND t.deleted = false
            ORDER BY t.nombre
            """)
    List<RolNotificacionTipoEntity> findActivasByRoleId(@Param("roleId") Long roleId);

    @Query("""
            SELECT a FROM RolNotificacionTipoEntity a
            JOIN FETCH a.tipo t
            WHERE a.role.id = :roleId
            """)
    List<RolNotificacionTipoEntity> findAllByRoleId(@Param("roleId") Long roleId);

    @Query("""
            SELECT COUNT(p)
            FROM ProfileEntity p
            JOIN p.module m
            WHERE p.role.id = :roleId
              AND p.deleted = false
              AND p.canRead = true
              AND m.deleted = false
              AND m.name = :moduleName
            """)
    long contarLecturaEnModulo(@Param("roleId") Long roleId, @Param("moduleName") String moduleName);

    @Query("""
            SELECT DISTINCT e FROM EmpleadosEntity e
            JOIN e.role r
            JOIN RolNotificacionTipoEntity a ON a.role.id = r.id
            JOIN a.tipo t
            JOIN r.roleProfile p
            JOIN p.module m
            WHERE e.deleted = false
              AND e.enabled = true
              AND r.deleted = false
              AND a.deleted = false
              AND t.deleted = false
              AND t.codigo = :codigo
              AND p.deleted = false
              AND p.canRead = true
              AND m.deleted = false
              AND m.name = :moduleName
            """)
    List<EmpleadosEntity> findDestinatarios(@Param("codigo") String codigo, @Param("moduleName") String moduleName);
}
