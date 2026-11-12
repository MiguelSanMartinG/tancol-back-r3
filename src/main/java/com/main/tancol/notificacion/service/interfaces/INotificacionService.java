package com.main.tancol.notificacion.service.interfaces;

import com.main.tancol.empleados.entities.EmpleadosEntity;
import com.main.tancol.notificacion.dto.response.NotificacionResponseDTO;
import com.main.tancol.notificacion.enums.TipoNotificacion;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface INotificacionService {

    int crearParaUsuarios(List<EmpleadosEntity> destinatarios,
                          String titulo,
                          String mensaje,
                          TipoNotificacion tipo,
                          UUID asignacionId,
                          UUID clienteId,
                          LocalDateTime periodoInicio,
                          LocalDateTime periodoFin);

    int crearMetaVenta(List<EmpleadosEntity> destinatarios,
                       String titulo,
                       String mensaje,
                       UUID metaVentaId,
                       LocalDateTime periodoInicio,
                       LocalDateTime periodoFin);

    int crearStockMinimo(List<EmpleadosEntity> destinatarios,
                         String titulo,
                         String mensaje,
                         UUID productoId);

    List<NotificacionResponseDTO> listarDelEmpleadoActual();

    NotificacionResponseDTO marcarLeida(UUID id);

    NotificacionResponseDTO crearDebug(String titulo, String mensaje);
}
