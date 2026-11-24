package com.main.tancol.inventariodiario.repositories;

import com.main.tancol.inventariodiario.entities.PartidaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PartidaRepository extends JpaRepository<PartidaEntity, UUID>, JpaSpecificationExecutor<PartidaEntity> {

    @Query(value = "SELECT nextval('inv_diario_partida_folio_seq')", nativeQuery = true)
    long siguienteFolio();
}
