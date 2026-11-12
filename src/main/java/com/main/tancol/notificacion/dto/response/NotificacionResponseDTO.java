package com.main.tancol.notificacion.dto.response;

import com.main.tancol.notificacion.enums.TipoNotificacion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Builder
public class NotificacionResponseDTO {
    private UUID id;
    private String titulo;
    private String mensaje;
    private String descripcion;
    private TipoNotificacion tipo;
    private boolean leida;
    private UUID asignacionId;
    private UUID clienteId;
    private UUID metaVentaId;
    private UUID productoId;
    private LocalDateTime periodoInicio;
    private LocalDateTime periodoFin;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
