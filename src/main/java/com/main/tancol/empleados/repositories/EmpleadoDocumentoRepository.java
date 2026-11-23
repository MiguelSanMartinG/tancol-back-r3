package com.main.tancol.empleados.repositories;

import com.main.tancol.empleados.entities.EmpleadoDocumentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EmpleadoDocumentoRepository extends JpaRepository<EmpleadoDocumentoEntity, UUID> {

    List<EmpleadoDocumentoEntity> findByEmpleado_IdAndDeletedFalseOrderByTipoAsc(UUID empleadoId);

    void deleteByEmpleado_Id(UUID empleadoId);
}
