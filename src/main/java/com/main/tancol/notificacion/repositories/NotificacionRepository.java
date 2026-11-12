package com.main.tancol.notificacion.repositories;

import com.main.tancol.notificacion.entities.NotificacionEntity;
import com.main.tancol.notificacion.enums.TipoNotificacion;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface NotificacionRepository extends CrudRepository<NotificacionEntity, UUID>,
        JpaSpecificationExecutor<NotificacionEntity> {

    List<NotificacionEntity> findByEmpleadoIdAndDeletedFalseAndTipoInOrderByCreatedAtDesc(
            UUID empleadoId, Collection<TipoNotificacion> tipos);

    List<NotificacionEntity> findByProductoIdAndTipoAndLeidaFalseAndDeletedFalse(UUID productoId, TipoNotificacion tipo);

    boolean existsByProductoIdAndTipoAndLeidaFalseAndDeletedFalse(UUID productoId, TipoNotificacion tipo);

    boolean existsByEmpleadoEvaluadoIdAndProductoIdAndTipoAndPeriodoInicioAndLeidaFalseAndDeletedFalse(
            UUID empleadoEvaluadoId,
            UUID productoId,
            TipoNotificacion tipo,
            LocalDateTime periodoInicio);
}
