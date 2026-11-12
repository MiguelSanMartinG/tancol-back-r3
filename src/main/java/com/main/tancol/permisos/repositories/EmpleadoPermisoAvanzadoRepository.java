package com.main.tancol.permisos.repositories;

import com.main.tancol.permisos.entities.EmpleadoPermisoAvanzadoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmpleadoPermisoAvanzadoRepository extends JpaRepository<EmpleadoPermisoAvanzadoEntity, UUID> {

    boolean existsByEmpleado_IdAndPermiso_CodigoAndDeletedFalse(UUID empleadoId, String codigo);

    List<EmpleadoPermisoAvanzadoEntity> findAllByDeletedFalse();

    List<EmpleadoPermisoAvanzadoEntity> findAllByEmpleado_IdAndDeletedFalse(UUID empleadoId);

    Optional<EmpleadoPermisoAvanzadoEntity> findByIdAndDeletedFalse(UUID id);

    Optional<EmpleadoPermisoAvanzadoEntity> findByEmpleado_IdAndPermiso_CodigoAndDeletedFalse(UUID empleadoId, String codigo);
}
