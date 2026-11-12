package com.main.tancol.reportes.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReporteBitacorasVentasResponseDTO {
    private UUID idBitacora;
    private UUID idTrayecto;
    private UUID idVenta;
    private String folio;
    private LocalDate fecha;
    private String estatusBitacora;
    private String nombreRuta;
    private String tipoProceso;
    private String chofer;
    private String ayudantes;
    private String nombreProducto;
    private String vehiculo;
    private LocalDateTime fechaFirma;
    private BigDecimal cantidadSalida;
    private BigDecimal cantidadLlegada;
    private BigDecimal cantidadVendida;
    private BigDecimal huecos;
    private BigDecimal rotos;
    private String fueraEspecificacion;
    private BigDecimal cantidadFueraEspecificacion;
    private String estadoVentaRuta;
    private Boolean mandadoALiquidacion;
    private String comentario;
}
