package com.main.tancol.permisos.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class PermisoAvanzadoResponseDTO {
    private UUID id;
    private String codigo;
    private String nombre;
    private String descripcion;
}
