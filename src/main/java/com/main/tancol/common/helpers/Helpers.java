package com.main.tancol.common.helpers;

import com.main.tancol.permisos.enums.ModuleEnum;
import com.main.tancol.permisos.enums.PermissionEnum;
import org.springframework.stereotype.Service;

@Service
public class Helpers {
    public static String authority(ModuleEnum module, PermissionEnum perm) {
        return module.name() + "_" + perm.name();
    }

}
