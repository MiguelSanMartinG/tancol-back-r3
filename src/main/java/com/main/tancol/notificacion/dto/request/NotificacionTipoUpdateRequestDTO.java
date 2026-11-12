package com.main.tancol.notificacion.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Value;

@Value
public class NotificacionTipoUpdateRequestDTO {

    @NotBlank
    @Size(max = 255)
    String nombre;

    @Size(max = 2000)
    String descripcion;
}
