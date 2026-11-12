package com.main.tancol.empleados.dto.request;

import lombok.*;

@Value
public class TiposEmpleadosRequestDTO {
    // Agrega campos aqu??
    private String nombre;
    private String alias;
    boolean enabled;
}
