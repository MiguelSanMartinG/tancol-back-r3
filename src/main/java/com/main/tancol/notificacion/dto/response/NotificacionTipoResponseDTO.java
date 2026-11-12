package com.main.tancol.notificacion.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Builder
public class NotificacionTipoResponseDTO {
    private UUID id;
    private String codigo;
    private String nombre;
    private String descripcion;
}
