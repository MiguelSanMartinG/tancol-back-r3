package com.main.tancol.permisos.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TienePermisoAvanzadoResponseDTO {
    private String codigo;
    private boolean tienePermiso;
}
