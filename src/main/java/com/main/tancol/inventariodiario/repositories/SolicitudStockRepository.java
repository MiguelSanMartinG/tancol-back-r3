package com.main.tancol.inventariodiario.repositories;

import com.main.tancol.inventariodiario.entities.SolicitudStockEntity;
import com.main.tancol.inventariodiario.enums.EstatusSolicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SolicitudStockRepository extends JpaRepository<SolicitudStockEntity, UUID>, JpaSpecificationExecutor<SolicitudStockEntity> {

    @Query(value = "SELECT nextval('inv_diario_solicitud_folio_seq')", nativeQuery = true)
    long siguienteFolio();

    Optional<SolicitudStockEntity> findFirstByArea_IdAndInsumo_IdAndEstatusInAndDeletedFalse(
            UUID areaId, UUID insumoId, Collection<EstatusSolicitud> estatus);
}
