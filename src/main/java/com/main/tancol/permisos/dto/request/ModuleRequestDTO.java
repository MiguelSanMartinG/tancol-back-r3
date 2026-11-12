package com.main.tancol.permisos.dto.request;

import lombok.Value;

import java.util.UUID;

@Value
public class ModuleRequestDTO {
    // Agrega campos aqu??
    String name;
    String rutaModulo;
    UUID parentId;
}
