package com.main.tancol.permisos.dto.request;

import com.main.tancol.permisos.enums.ModuleEnum;
import lombok.Value;

import java.util.Set;

@Value
public class RoleRequestDTO {
    // Agrega campos aqu??
    String roleName;
//    Set<RoleModulePermissionRequestDTO> permissions;
}
