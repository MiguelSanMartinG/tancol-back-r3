package com.main.tancol.dashboard.repositories;

import com.main.tancol.dashboard.entities.DashboardKpiConfiguracionEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DashboardKpiConfiguracionRepository extends CrudRepository<DashboardKpiConfiguracionEntity, UUID> {

    Optional<DashboardKpiConfiguracionEntity> findByEmpleadoId(UUID empleadoId);
}
