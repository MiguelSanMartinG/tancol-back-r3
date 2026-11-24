package com.main.tancol.reportes.dto.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.main.tancol.common.util.RangoFechasDeserializer;
import com.main.tancol.liquidacion.enums.FormaPagoLiquidacion;
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
public class ReporteLiquidacionesRequestDTO {
    private List<UUID> idClientes;
    private List<UUID> idRutas;
    private List<UUID> idProductosComerciales;
    @JsonDeserialize(using = RangoFechasDeserializer.Inicio.class)
    private LocalDateTime fechaInicio;
    @JsonDeserialize(using = RangoFechasDeserializer.Fin.class)
    private LocalDateTime fechaFin;
    private List<FormaPagoLiquidacion> tipoPago;
    private List<UUID> conceptosVenta;
    private Boolean facturado;
    private String estadoLiquidacion;
    private String x;

}
