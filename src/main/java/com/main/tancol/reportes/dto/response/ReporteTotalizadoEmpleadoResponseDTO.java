package com.main.tancol.reportes.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReporteTotalizadoEmpleadoResponseDTO {
    private int idEmpleado;
    private String nombreEmpleado;
    private String nombreProducto;
    private Long cantidadVendida;
}
