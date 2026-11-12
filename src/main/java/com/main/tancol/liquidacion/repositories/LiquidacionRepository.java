package com.main.tancol.liquidacion.repositories;

import com.main.tancol.liquidacion.entities.LiquidacionEntity;
import com.main.tancol.liquidacion.enums.EstadosLiquidacion;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface LiquidacionRepository extends CrudRepository<LiquidacionEntity, UUID>, JpaSpecificationExecutor<LiquidacionEntity>{

//    boolean existsByFechaAndRuta_Id(LocalDate fecha, UUID id);

    LiquidacionEntity findByFechaAndRuta_IdAndEstadosLiquidacionNot(LocalDateTime fecha, UUID id, EstadosLiquidacion estadosLiquidacion);

    boolean existsByFechaAndRuta_IdAndEstadosLiquidacionNot(LocalDateTime fecha, UUID id, EstadosLiquidacion estadosLiquidacion);

    boolean existsByFechaBetweenAndRuta_IdAndEstadosLiquidacionNot(LocalDateTime fechaInicio,LocalDateTime fechaFin,UUID rutaId,EstadosLiquidacion estado);

    LiquidacionEntity findByFechaBetweenAndRuta_IdAndEstadosLiquidacionNot(LocalDateTime fechaInicio,LocalDateTime fechaFin, UUID id, EstadosLiquidacion estadosLiquidacion);

    boolean existsByFechaBetweenAndRuta_IdAndEstadosLiquidacionNotIn(
            LocalDateTime fechaInicio, LocalDateTime fechaFin, UUID rutaId, Collection<EstadosLiquidacion> estados);

    LiquidacionEntity findByFechaBetweenAndRuta_IdAndEstadosLiquidacionNotIn(
            LocalDateTime fechaInicio, LocalDateTime fechaFin, UUID rutaId, Collection<EstadosLiquidacion> estados);

    List<LiquidacionEntity> findByFechaBetweenAndRuta_Id(
            LocalDateTime fechaInicio, LocalDateTime fechaFin, UUID rutaId);
}
