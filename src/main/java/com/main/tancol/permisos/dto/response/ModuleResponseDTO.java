package com.main.tancol.permisos.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Builder
public class ModuleResponseDTO {
    private UUID idModulo;
    private String name;
    private String ruta;
    private ModuleResponseDTO parent;
    // Agrega m??s campos aqu??
}
