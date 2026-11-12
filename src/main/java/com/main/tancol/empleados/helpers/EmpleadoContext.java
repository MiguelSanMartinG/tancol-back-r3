package com.main.tancol.empleados.helpers;

import com.main.tancol.empleados.entities.EmpleadosEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class EmpleadoContext {
    public static EmpleadosEntity getEmpleadoActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }

        Object principal = auth.getPrincipal();

        if (principal instanceof EmpleadosEntity empleado) {
            return empleado;
        }

        return null;
    }
}
