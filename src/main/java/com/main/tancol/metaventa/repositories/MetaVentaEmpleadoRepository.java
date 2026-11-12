package com.main.tancol.metaventa.repositories;

import com.main.tancol.metaventa.entities.MetaVentaEmpleadoEntity;
import com.main.tancol.metaventa.enums.PeriodoMetaVenta;
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
public interface MetaVentaEmpleadoRepository extends CrudRepository<MetaVentaEmpleadoEntity, UUID>,
        JpaSpecificationExecutor<MetaVentaEmpleadoEntity> {

    boolean existsByEmpleadoIdAndProductoIdAndActivaTrueAndDeletedFalse(UUID empleadoId, UUID productoId);

    @Query("""
            SELECT m FROM MetaVentaEmpleadoEntity m
            JOIN FETCH m.empleado
            JOIN FETCH m.producto
            WHERE m.activa = true
              AND m.deleted = false
              AND m.periodo = :periodo
              AND m.cantidadMinima IS NOT NULL
            """)
    List<MetaVentaEmpleadoEntity> findActivasByPeriodoConCantidad(@Param("periodo") PeriodoMetaVenta periodo);

    @Query("""
            SELECT m FROM MetaVentaEmpleadoEntity m
            JOIN FETCH m.empleado
            JOIN FETCH m.producto
            WHERE m.deleted = false
              AND m.periodo = :periodo
              AND m.cantidadMinima IS NOT NULL
              AND (m.fechaInicio IS NULL OR m.fechaInicio < :fin)
              AND (m.fechaCierre IS NULL OR m.fechaCierre > :inicio)
            """)
    List<MetaVentaEmpleadoEntity> findVigentesEnPeriodo(@Param("periodo") PeriodoMetaVenta periodo,
                                                        @Param("inicio") LocalDateTime inicio,
                                                        @Param("fin") LocalDateTime fin);

    @Query("""
            SELECT m FROM MetaVentaEmpleadoEntity m
            JOIN FETCH m.empleado
            JOIN FETCH m.producto
            WHERE m.deleted = false
              AND m.cantidadMinima IS NOT NULL
              AND m.producto.id IN :productoIds
              AND (m.fechaInicio IS NULL OR m.fechaInicio < :fin)
              AND (m.fechaCierre IS NULL OR m.fechaCierre > :inicio)
            """)
    List<MetaVentaEmpleadoEntity> findVigentesPorProductos(@Param("productoIds") Collection<UUID> productoIds,
                                                           @Param("inicio") LocalDateTime inicio,
                                                           @Param("fin") LocalDateTime fin);
}
