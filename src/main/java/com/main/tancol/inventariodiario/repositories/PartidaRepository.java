package com.main.tancol.inventariodiario.repositories;

import com.main.tancol.inventariodiario.entities.PartidaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Repository
public interface PartidaRepository extends JpaRepository<PartidaEntity, UUID>, JpaSpecificationExecutor<PartidaEntity> {

    @Query(value = "SELECT nextval('inv_diario_partida_folio_seq')", nativeQuery = true)
    long siguienteFolio();

    /** Lo registrado sin firmar (y aún sin descontar) de un insumo del área en un rango de fechas. */
    @Query("""
            SELECT COALESCE(SUM(p.cantidad), 0) FROM PartidaEntity p
            WHERE p.area.id = :areaId AND p.insumo.id = :insumoId AND p.deleted = false
              AND p.idImagenFirma IS NULL AND p.descontado = false
              AND p.fecha >= :desde AND p.fecha < :hasta
              AND p.id <> :excluir
            """)
    BigDecimal pendienteFirma(@Param("areaId") UUID areaId, @Param("insumoId") UUID insumoId,
                              @Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta,
                              @Param("excluir") UUID excluir);
}
