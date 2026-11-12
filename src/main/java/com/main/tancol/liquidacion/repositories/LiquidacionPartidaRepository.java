package com.main.tancol.liquidacion.repositories;

import com.main.tancol.liquidacion.entities.LiquidacionPartidaEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LiquidacionPartidaRepository extends CrudRepository<LiquidacionPartidaEntity, UUID>, JpaSpecificationExecutor<LiquidacionPartidaEntity> {

    Optional<LiquidacionPartidaEntity> findFirstByLiquidacionOrigen_BloqueNotaAndFolioNotNullOrderByFolioDesc(String bloqueNota);

    @Query("""
            SELECT COALESCE(SUM(lp.importe), 0)
            FROM LiquidacionPartidaEntity lp
            WHERE lp.cliente.id = :clienteId
              AND lp.cancelada = false
              AND lp.liquidacionOrigen.estadosLiquidacion = com.main.tancol.liquidacion.enums.EstadosLiquidacion.FINALIZADA
              AND lp.liquidacionOrigen.fecha >= :inicio
              AND lp.liquidacionOrigen.fecha < :fin
            """)
    BigDecimal sumImporteByClienteAndFechaBetween(@Param("clienteId") UUID clienteId,
                                                  @Param("inicio") LocalDateTime inicio,
                                                  @Param("fin") LocalDateTime fin);
}
