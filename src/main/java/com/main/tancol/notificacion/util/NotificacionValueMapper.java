package com.main.tancol.notificacion.util;

import com.main.tancol.notificacion.dto.response.NotificacionResponseDTO;
import com.main.tancol.notificacion.entities.NotificacionEntity;

public class NotificacionValueMapper {

    public static NotificacionResponseDTO entityToResponse(NotificacionEntity entity, String descripcion) {
        if (entity == null) {
            return null;
        }
        return NotificacionResponseDTO.builder()
                .id(entity.getId())
                .titulo(entity.getTitulo())
                .mensaje(entity.getMensaje())
                .descripcion(descripcion)
                .tipo(entity.getTipo())
                .leida(entity.isLeida())
                .asignacionId(entity.getAsignacionId())
                .clienteId(entity.getClienteId())
                .metaVentaId(entity.getMetaVentaId())
                .productoId(entity.getProductoId())
                .periodoInicio(entity.getPeriodoInicio())
                .periodoFin(entity.getPeriodoFin())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
