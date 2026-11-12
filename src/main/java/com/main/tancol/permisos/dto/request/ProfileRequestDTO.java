package com.main.tancol.permisos.dto.request;

import lombok.*;

import java.util.UUID;

@Value
public class ProfileRequestDTO {
    // Agrega campos aqu??
    UUID idModulo;
    Long idRol;
    boolean canUpdate;
    boolean canDelete;
    boolean canCreate;
    boolean canRead;
}
