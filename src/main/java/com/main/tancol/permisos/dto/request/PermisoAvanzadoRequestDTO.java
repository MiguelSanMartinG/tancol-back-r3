package com.main.tancol.permisos.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Value;

@Value
public class PermisoAvanzadoRequestDTO {

    @NotBlank
    @Size(max = 100)
    String codigo;

    @NotBlank
    @Size(max = 255)
    String nombre;

    @Size(max = 500)
    String descripcion;
}
