package com.main.tancol.reportes.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReporteVVEDiaResponseDTO {
    private Date fecha;
    private Long idEmpleado;
    private String nombre;
    private String producto;
    private BigDecimal cantidadVendida;
    private BigDecimal importe;
}
