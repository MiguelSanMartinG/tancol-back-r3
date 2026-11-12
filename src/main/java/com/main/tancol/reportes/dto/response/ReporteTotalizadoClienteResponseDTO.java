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
public class ReporteTotalizadoClienteResponseDTO {
    private String alias;
    private String nombre;
    private String nombreComercial;
    private String razonSocial;
    private BigDecimal cantidadVendida;
}
