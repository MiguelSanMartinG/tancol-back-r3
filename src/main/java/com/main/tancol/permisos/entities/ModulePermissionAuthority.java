package com.main.tancol.permisos.entities;

import org.springframework.security.core.GrantedAuthority;

public class ModulePermissionAuthority implements GrantedAuthority {

    private final String authority;

    public ModulePermissionAuthority(String moduleName, String action) {
        this.authority = moduleName + ":" + action.toUpperCase(); // ej: "Ventas:READ"
    }

    @Override
    public String getAuthority() {
        return authority;
    }

    @Override
    public String toString() {
        return authority;
    }
}