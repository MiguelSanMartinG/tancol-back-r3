package com.main.tancol.reportes.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReporteGeneralCorridasResponseDTO {
    @Builder.Default
    private List<String> columnas = new ArrayList<>();
    @Builder.Default
    private List<ReporteGeneralCorridasFilaResponseDTO> filas = new ArrayList<>();
    @Builder.Default
    private Map<String, BigDecimal> totalesPorColumna = new LinkedHashMap<>();
    private BigDecimal granTotal;
}
