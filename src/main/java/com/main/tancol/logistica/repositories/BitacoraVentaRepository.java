package com.main.tancol.logistica.repositories;

import com.main.tancol.logistica.entities.bitacora.BitacoraVentaRutaEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface BitacoraVentaRepository extends CrudRepository<BitacoraVentaRutaEntity, UUID>, JpaSpecificationExecutor<BitacoraVentaRutaEntity>{
    // Agrega m??todos personalizados aqu??
    boolean existsByFecha(LocalDate fecha);

    boolean existsByCreatedAt(LocalDateTime createdAt);

    BitacoraVentaRutaEntity findByFecha(LocalDate fecha);
}
