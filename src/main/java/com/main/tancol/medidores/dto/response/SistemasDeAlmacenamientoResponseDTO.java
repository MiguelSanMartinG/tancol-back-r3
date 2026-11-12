package com.main.tancol.medidores.dto.response;

import com.main.tancol.empleados.dto.response.DepartamentoResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Builder
public class SistemasDeAlmacenamientoResponseDTO {

    private DepartamentoResponseDTO departamento;
    private BigDecimal capacidad;
    private UUID id;
    private String nombre;
    private String alias;
    private boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    // Agrega m??s campos aqu??
}
