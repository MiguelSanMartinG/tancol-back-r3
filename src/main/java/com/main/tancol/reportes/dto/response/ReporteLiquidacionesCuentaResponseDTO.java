package com.main.tancol.reportes.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReporteLiquidacionesCuentaResponseDTO {
    private String folio;
    private String bloqueNota;
    private Long folioNota;
    private String nombreRuta;
    private String nombreProducto;
    private BigDecimal cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal descuentoEfectuado;
    private BigDecimal importe;
    private BigDecimal cuentaB;
    private BigDecimal cuentaA;
    private String nombreCliente;
    private String conceptoVenta;
    private Boolean f1;
    private Boolean descuento;
    private Boolean cancelados;
    private String tipoPago;
    private String estadoLiquidacion;
    private String vendedor_chofer;
    private String ayudantes;
    private LocalDateTime fecha;
}
