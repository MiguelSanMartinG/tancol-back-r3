package com.main.tancol.medidores.repositories;

import com.main.tancol.medidores.entities.BitacoraMedicionEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BitacoraMedicionRepository extends CrudRepository<BitacoraMedicionEntity, UUID>, JpaSpecificationExecutor<BitacoraMedicionEntity> {

    Optional<BitacoraMedicionEntity> findTopByMedidor_IdMedidorAndCreatedAtBeforeOrderByCreatedAtDesc(
            UUID medidorId,
            LocalDateTime before
    );

    List<BitacoraMedicionEntity> findByMedidor_IdMedidorInAndCreatedAtGreaterThanEqualAndCreatedAtLessThanOrderByCreatedAtDesc(
            Collection<UUID> medidorIds,
            LocalDateTime inicio,
            LocalDateTime fin
    );
}
