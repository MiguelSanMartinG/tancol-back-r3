package com.main.tancol.liquidacion.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Builder
public class ConceptosVentasResponseDTO {
    private UUID id;
    private String name;
    private String concepto;
    private boolean x;
    private boolean cancelado;
    private boolean descuento;
    private boolean exclude;
    private boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    // Agrega m??s campos aqu??
}
