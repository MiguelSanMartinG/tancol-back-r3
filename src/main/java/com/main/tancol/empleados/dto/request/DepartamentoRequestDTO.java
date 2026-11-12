package com.main.tancol.empleados.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Value;

@Value
public class DepartamentoRequestDTO {
    @NotBlank
     String nombre;
    @NotBlank
     String alias;
     String descripcion;
     boolean estado;
}
