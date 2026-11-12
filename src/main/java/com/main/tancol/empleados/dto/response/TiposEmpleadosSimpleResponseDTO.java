package com.main.tancol.empleados.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Getter
@SuperBuilder
public class TiposEmpleadosSimpleResponseDTO {
    private UUID idTipoEmpleado;
    private String nombre;
    private String alias;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean enabled;
}
