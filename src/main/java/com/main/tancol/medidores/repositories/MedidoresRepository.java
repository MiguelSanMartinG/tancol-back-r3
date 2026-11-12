package com.main.tancol.medidores.repositories;

import com.main.tancol.medidores.entities.MedidoresEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MedidoresRepository extends JpaRepository<MedidoresEntity, UUID>, JpaSpecificationExecutor<MedidoresEntity> {

    @EntityGraph(attributePaths = "medidoresHijos")
    @Override
    Optional<MedidoresEntity> findById(UUID id);

    @EntityGraph(attributePaths = "medidoresHijos")
    @Override
    List<MedidoresEntity> findAll(Specification<MedidoresEntity> spec);

    List<MedidoresEntity> findByMedidorPadre_IdMedidorIn(Collection<UUID> padreIds);

    @EntityGraph(attributePaths = "medidoresHijos")
    List<MedidoresEntity> findByIdMedidorIn(Collection<UUID> ids);
}
