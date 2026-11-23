package com.main.tancol.empleados.dto.request;

import com.main.tancol.empleados.enums.TipoContratacion;
import java.time.LocalDate;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;
import com.main.tancol.empleados.dto.EmpleadoRhDTOs;
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
    String nss;
    String rfc;
    String curp;
    LocalDate fechaIngreso;
    LocalDate fechaEgreso;
    String observaciones;
    TipoContratacion tipoContratacion;
    LocalDate fechaVigencia;

    // Datos de RH (opcionales)
    String nacionalidad;
    String sexo;
    String estadoCivil;
    String tipoSangre;
    EmpleadoRhDTOs.Domicilio domicilio;
    /** null = no se modifican; lista (aunque vacía) = reemplaza a los guardados. */
    List<EmpleadoRhDTOs.ContactoEmergencia> contactosEmergencia;
    /** null = no se modifican; lista (aunque vacía) = reemplaza a los guardados. */
    List<EmpleadoRhDTOs.Documento> documentos;
}
