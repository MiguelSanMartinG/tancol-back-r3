package com.main.tancol.empleados.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Builder
public class EmpleadosBasicResponseDTO {
    private UUID idEmpleado;
    private String nombre;
    private Boolean enabled;
    private String nombreUsuario;
    private DepartamentoSimpleResponseDTO departamento;
    private TiposEmpleadosSimpleResponseDTO tipoEmpleado;
    // Agrega m??s campos aqu??
}
