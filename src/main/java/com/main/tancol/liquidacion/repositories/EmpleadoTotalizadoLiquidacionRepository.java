package com.main.tancol.liquidacion.repositories;

import com.main.tancol.liquidacion.dto.VentaEmpleadoProductoAgg;
import com.main.tancol.liquidacion.entities.LiquidacionTotalizadoEmpleadoEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface EmpleadoTotalizadoLiquidacionRepository extends CrudRepository<LiquidacionTotalizadoEmpleadoEntity, UUID>, JpaSpecificationExecutor<LiquidacionTotalizadoEmpleadoEntity>{

    @Query("""
            SELECT COALESCE(SUM(lte.cantidad), 0)
            FROM LiquidacionTotalizadoEmpleadoEntity lte
            WHERE lte.empleado.id = :empleadoId
              AND lte.origenTotalizado.productoComercial.id = :productoId
              AND lte.origenTotalizado.liquidacionOrigen.estadosLiquidacion = com.main.tancol.liquidacion.enums.EstadosLiquidacion.FINALIZADA
              AND lte.origenTotalizado.liquidacionOrigen.fecha >= :inicio
              AND lte.origenTotalizado.liquidacionOrigen.fecha < :fin
            """)
    BigDecimal sumCantidadByEmpleadoAndProductoAndFechaBetween(@Param("empleadoId") UUID empleadoId,
                                                               @Param("productoId") UUID productoId,
                                                               @Param("inicio") LocalDateTime inicio,
                                                               @Param("fin") LocalDateTime fin);

    @Query("""
            SELECT new com.main.tancol.liquidacion.dto.VentaEmpleadoProductoAgg(
                e.id, e.nombre, e.primerApellido, e.segundoApellido,
                p.id, p.nombre, p.alias, SUM(lte.cantidad))
            FROM LiquidacionTotalizadoEmpleadoEntity lte
            JOIN lte.empleado e
            JOIN lte.origenTotalizado ot
            JOIN ot.productoComercial p
            JOIN ot.liquidacionOrigen l
            WHERE p.id IN :productoIds
              AND l.deleted = false
              AND l.estadosLiquidacion = com.main.tancol.liquidacion.enums.EstadosLiquidacion.FINALIZADA
              AND l.fecha >= :inicio
              AND l.fecha < :fin
            GROUP BY e.id, e.nombre, e.primerApellido, e.segundoApellido, p.id, p.nombre, p.alias
            """)
    List<VentaEmpleadoProductoAgg> sumarVentasPorEmpleadoYProducto(@Param("productoIds") Collection<UUID> productoIds,
                                                                   @Param("inicio") LocalDateTime inicio,
                                                                   @Param("fin") LocalDateTime fin);
}
