package com.main.tancol.reportes.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReporteVolumenVentasResponseDTO {
    private Long idEmpleado;
    private String nombre;
    private String producto;
    private BigDecimal cantidadVendida;
}
