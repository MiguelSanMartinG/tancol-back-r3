package com.main.tancol.logistica.repositories;

import com.main.tancol.logistica.entities.bitacora.BitacoraVentaRutaEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface BitacoraVentaRepository extends CrudRepository<BitacoraVentaRutaEntity, UUID>, JpaSpecificationExecutor<BitacoraVentaRutaEntity>{
    // Agrega m??todos personalizados aqu??
    boolean existsByFecha(LocalDate fecha);

    boolean existsByCreatedAt(LocalDateTime createdAt);

    BitacoraVentaRutaEntity findByFecha(LocalDate fecha);

    /** Numero de trayectos (partidas) por bitacora para una pagina: [idBitacora, numeroPartidas]. */
    @Query("select t.bitacoraOrigen.idBitacora, count(t) from TrayectoEntity t " +
            "where t.bitacoraOrigen.idBitacora in :ids group by t.bitacoraOrigen.idBitacora")
    List<Object[]> contarPartidas(@Param("ids") Collection<UUID> ids);
}
