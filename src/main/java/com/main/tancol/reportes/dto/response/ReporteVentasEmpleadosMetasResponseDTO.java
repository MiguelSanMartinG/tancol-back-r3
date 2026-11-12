package com.main.tancol.reportes.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReporteVentasEmpleadosMetasResponseDTO {
    private UUID empleadoId;
    private String empleadoNombre;
    private UUID productoId;
    private String productoNombre;
    private String productoAlias;
    private BigDecimal ventaMinima;
    private BigDecimal ventaSemana;
    private BigDecimal alcanceObjetivo;
    private BigDecimal comisionSugerida;
}
