package com.main.tancol.reportes.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReporteGeneralCorridasFilaResponseDTO {
    private UUID empleadoId;
    private String nombreEmpleado;
    @Builder.Default
    private Map<String, BigDecimal> columnas = new LinkedHashMap<>();
    private BigDecimal totalFila;
}
