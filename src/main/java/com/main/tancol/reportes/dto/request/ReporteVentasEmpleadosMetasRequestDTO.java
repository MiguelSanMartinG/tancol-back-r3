package com.main.tancol.reportes.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReporteVentasEmpleadosMetasRequestDTO {
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private List<UUID> idProductos;
}
