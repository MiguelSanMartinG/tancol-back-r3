package com.main.tancol.permisos.repositories;

import com.main.tancol.permisos.entities.PermisoAvanzadoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PermisoAvanzadoRepository extends JpaRepository<PermisoAvanzadoEntity, UUID> {

    Optional<PermisoAvanzadoEntity> findByCodigoAndDeletedFalse(String codigo);

    List<PermisoAvanzadoEntity> findAllByDeletedFalse();

    boolean existsByCodigoAndDeletedFalse(String codigo);
}
