package com.main.tancol.permisos.service;

import com.main.tancol.empleados.entities.EmpleadosEntity;
import com.main.tancol.permisos.entities.ProfileEntity;
import com.main.tancol.permisos.repositories.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final ProfileRepository repo;
    private final PermisoAvanzadoService permisoAvanzadoService;

    public boolean hasPermission(EmpleadosEntity user, String moduleName, String action) {
        Optional<ProfileEntity> permOpt =
                repo.findByRoleIdAndModuleName(user.getRole().getId(), moduleName);

        if (permOpt.isEmpty()) return false;

        ProfileEntity perm = permOpt.get();

        return switch (action.toLowerCase()) {
            case "read" -> perm.isCanRead();
            case "write" -> perm.isCanWrite();
            case "delete" -> perm.isCanDelete();
            case "update" -> perm.isCanUpdate();
            default -> false;
        };
    }

    public boolean hasAdvancedPermission(EmpleadosEntity user, String codigo) {
        return permisoAvanzadoService.hasAdvancedPermission(user, codigo);
    }
}
