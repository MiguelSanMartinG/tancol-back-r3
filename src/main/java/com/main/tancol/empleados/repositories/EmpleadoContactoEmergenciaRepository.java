package com.main.tancol.empleados.repositories;

import com.main.tancol.empleados.entities.EmpleadoContactoEmergenciaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EmpleadoContactoEmergenciaRepository extends JpaRepository<EmpleadoContactoEmergenciaEntity, UUID> {

    List<EmpleadoContactoEmergenciaEntity> findByEmpleado_IdAndDeletedFalseOrderByOrdenAsc(UUID empleadoId);

    void deleteByEmpleado_Id(UUID empleadoId);
}
