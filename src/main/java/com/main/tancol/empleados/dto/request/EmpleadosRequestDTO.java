package com.main.tancol.empleados.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.UUID;

@Value
@Builder
public class EmpleadosRequestDTO {
    @NotBlank
    String nombre;
    @NotBlank
    String apellidoMaterno;
    @NotBlank
    String apellidoPaterno;
    UUID idTipoEmpleado;
    UUID idDepartamento;
    String correo;
    String telefono;
    LocalDateTime fechaNacimiento;
    String nombreUsuario;
    String password;
    Long idRol;
    boolean enabled;
}
