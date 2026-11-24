package com.main.tancol.inventariodiario.repositories;

import com.main.tancol.inventariodiario.entities.ConfirmacionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConfirmacionRepository extends JpaRepository<ConfirmacionEntity, UUID> {

    Optional<ConfirmacionEntity> findByArea_IdAndFechaAndDeletedFalse(UUID areaId, LocalDate fecha);
}
