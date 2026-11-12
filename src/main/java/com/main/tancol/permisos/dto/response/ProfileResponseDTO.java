package com.main.tancol.permisos.dto.response;

import com.main.tancol.permisos.dto.request.ModuleRequestDTO;
import lombok.*;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Builder
public class ProfileResponseDTO {
    private UUID idPerfil;
    private ModuleResponseDTO module;
    private RoleResponseDTO role;

    private boolean canRead;
    private boolean canWrite;
    private boolean canDelete;
    private boolean canUpdate;

    // Agrega m??s campos aqu??
}
