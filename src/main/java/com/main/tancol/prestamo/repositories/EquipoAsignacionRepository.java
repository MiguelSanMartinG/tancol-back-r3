package com.main.tancol.prestamo.repositories;

import com.main.tancol.prestamo.entities.EquipoAsignacionEntity;
import com.main.tancol.prestamo.enums.PeriodoCoberturaEquipo;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EquipoAsignacionRepository extends CrudRepository<EquipoAsignacionEntity, UUID>,
        JpaSpecificationExecutor<EquipoAsignacionEntity> {

    boolean existsByEquipoIdAndActivaTrueAndDeletedFalse(UUID equipoId);

    Optional<EquipoAsignacionEntity> findByEquipoIdAndActivaTrueAndDeletedFalse(UUID equipoId);

    @Query("""
            SELECT a FROM EquipoAsignacionEntity a
            JOIN FETCH a.cliente
            JOIN FETCH a.equipo
            WHERE a.activa = true
              AND a.deleted = false
              AND a.equipo.id IN :equipoIds
            """)
    List<EquipoAsignacionEntity> findActivasByEquipoIdIn(@Param("equipoIds") List<UUID> equipoIds);

    @Query("""
            SELECT a FROM EquipoAsignacionEntity a
            JOIN FETCH a.equipo
            JOIN FETCH a.cliente
            WHERE a.activa = true
              AND a.deleted = false
              AND a.periodoCobertura = :periodo
              AND a.montoMinimo IS NOT NULL
            """)
    List<EquipoAsignacionEntity> findActivasByPeriodoConMonto(@Param("periodo") PeriodoCoberturaEquipo periodo);
}
