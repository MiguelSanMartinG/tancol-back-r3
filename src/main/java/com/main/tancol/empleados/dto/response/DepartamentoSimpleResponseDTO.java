package com.main.tancol.empleados.dto.response;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for {@link com.main.tancol.empleados.entities.DepartamentoEntity}
 */
@Builder
@Value
@AllArgsConstructor
public class DepartamentoSimpleResponseDTO implements Serializable {
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    boolean enabled;
    UUID idDepartamento;
    @NotBlank(message = "Debe tener un nombre")
    String nombre;
    @NotBlank
    String alias;
    String descripcion;
}