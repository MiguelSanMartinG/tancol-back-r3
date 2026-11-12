package com.main.tancol.notificacion.repositories;

import com.main.tancol.notificacion.entities.NotificacionTipoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NotificacionTipoRepository extends JpaRepository<NotificacionTipoEntity, UUID> {

    List<NotificacionTipoEntity> findAllByDeletedFalseOrderByNombreAsc();

    Optional<NotificacionTipoEntity> findByCodigoAndDeletedFalse(String codigo);
}
