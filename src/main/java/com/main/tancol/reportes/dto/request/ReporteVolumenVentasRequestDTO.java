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
public class ReporteVolumenVentasRequestDTO {
    private List<UUID> idEmpledos;
    private List<UUID> idProductos;
    private List<UUID> idRutas;
    private List<UUID> conceptosVenta;
    private Boolean facturado;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
}
