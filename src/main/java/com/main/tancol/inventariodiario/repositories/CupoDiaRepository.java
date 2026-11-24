package com.main.tancol.inventariodiario.repositories;

import com.main.tancol.inventariodiario.entities.CupoDiaEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CupoDiaRepository extends JpaRepository<CupoDiaEntity, UUID> {

    Optional<CupoDiaEntity> findByCupo_IdAndFecha(UUID cupoId, LocalDate fecha);

    /** Con bloqueo para que dos partidas simultáneas no excedan el cupo. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT d FROM CupoDiaEntity d WHERE d.cupo.id = :cupoId AND d.fecha = :fecha")
    Optional<CupoDiaEntity> bloquear(@Param("cupoId") UUID cupoId, @Param("fecha") LocalDate fecha);
}
