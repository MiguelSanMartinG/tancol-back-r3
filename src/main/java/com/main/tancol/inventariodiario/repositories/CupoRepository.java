package com.main.tancol.inventariodiario.repositories;

import com.main.tancol.inventariodiario.entities.CupoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CupoRepository extends JpaRepository<CupoEntity, UUID> {

    List<CupoEntity> findByArea_IdAndDeletedFalseOrderByPorDefectoDescCreatedAtAsc(UUID areaId);

    Optional<CupoEntity> findFirstByArea_IdAndInsumo_IdAndDeletedFalse(UUID areaId, UUID insumoId);

    boolean existsByArea_IdAndDeletedFalse(UUID areaId);
}
