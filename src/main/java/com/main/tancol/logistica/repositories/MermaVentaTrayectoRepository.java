package com.main.tancol.logistica.repositories;

import com.main.tancol.logistica.entities.bitacora.MermaVentaTrayectoEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface MermaVentaTrayectoRepository extends CrudRepository<MermaVentaTrayectoEntity, UUID> {

    @Query("""
            SELECT COALESCE(SUM(m.cantidad), 0)
            FROM MermaVentaTrayectoEntity m
            WHERE m.rotoPorMaquina = false
              AND m.empleadoResponsable.id = :empleadoId
              AND m.ventaOrigen.producto.id = :productoId
              AND m.createdAt >= :inicio
              AND m.createdAt <= :fin
            """)
    BigDecimal sumRotosEmpleadoProductoEntre(@Param("empleadoId") UUID empleadoId,
                                              @Param("productoId") UUID productoId,
                                              @Param("inicio") LocalDateTime inicio,
                                              @Param("fin") LocalDateTime fin);
}
