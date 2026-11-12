package com.main.tancol.empleados.dto.response;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Value
@Builder
public class DepartamentoResponseDTO {
    private UUID idDepartamento;
    private String nombre;
    private String alias;
    private String descripcion;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean status;
    private List<EmpleadosResponseDTO> empleados;
}
