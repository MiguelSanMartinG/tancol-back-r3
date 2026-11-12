package com.main.tancol.permisos.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class AsignacionPermisoAvanzadoResponseDTO {
    private UUID id;
    private UUID empleadoId;
    private String nombreUsuario;
    private String nombreEmpleado;
    private UUID permisoId;
    private String codigo;
    private String nombrePermiso;
    private String asignadoPor;
}
