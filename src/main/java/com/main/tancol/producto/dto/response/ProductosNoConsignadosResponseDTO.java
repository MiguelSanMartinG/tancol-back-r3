package com.main.tancol.producto.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Builder
public class ProductosNoConsignadosResponseDTO {
    private UUID idProductoNoConsignados;
    private BigDecimal cantidad;
    private ConceptosResponseDTO conceptos;
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;
    // Agrega m??s campos aqu??
}
