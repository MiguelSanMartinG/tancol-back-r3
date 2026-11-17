package com.main.tancol.empleados.dto.response;

import com.main.tancol.empleados.enums.TipoContratacion;
import java.time.LocalDate;
import com.main.tancol.permisos.dto.response.RoleResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Builder
public class EmpleadosResponseDTO {
    private UUID idEmpleado;
    private RoleResponseDTO role;
    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String email;
    private LocalDateTime fechaNacimiento;
    private String telefono;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean enabled;
    private String nombreUsuario;
    private DepartamentoSimpleResponseDTO departamento;
    private TiposEmpleadosSimpleResponseDTO tipoEmpleado;
    private String nss;
    private String rfc;
    private String curp;
    private LocalDate fechaIngreso;
    private LocalDate fechaEgreso;
    private String observaciones;
    private TipoContratacion tipoContratacion;
    private LocalDate fechaVigencia;
    // Agrega m??s campos aqu??
}
