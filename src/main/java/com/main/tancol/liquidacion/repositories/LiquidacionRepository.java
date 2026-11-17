package com.main.tancol.liquidacion.repositories;

import com.main.tancol.liquidacion.entities.LiquidacionEntity;
import com.main.tancol.liquidacion.enums.EstadosLiquidacion;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
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

    /**
     * Resumen de partidas para una pagina de liquidaciones: [idLiquidacion, numeroPartidas, totalEstimado].
     * Una sola consulta agrupada en lugar de cargar las partidas de cada fila.
     */
    @Query("select p.liquidacionOrigen.idLiquidacion, count(p), " +
            "sum(coalesce(p.precio, 0) * coalesce(p.cantidad, 0)) " +
            "from LiquidacionPartidaEntity p " +
            "where p.liquidacionOrigen.idLiquidacion in :ids " +
            "group by p.liquidacionOrigen.idLiquidacion")
    List<Object[]> resumenPartidas(@Param("ids") Collection<UUID> ids);
}
