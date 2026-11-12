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
public class ReporteLiquidacionesResponseDTO {
    private String folio;
    private String bloqueNota;
    private Long folioNota;
    private String nombreRuta;
    private String nombreProducto;
    private BigDecimal cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal descuentoEfectuado;
    private BigDecimal importe;
    private BigDecimal cuentaA;
    private BigDecimal cuentaB;
    private String nombreCliente;
    private String conceptoVenta;
    private Boolean f1;
    private Boolean descuento;
    private Boolean cancelados;
    private Boolean facturados;
    private String tipoPago;
    private String estadoLiquidacion;
    private String vendedor_chofer;
    private String ayudantes;
    private LocalDateTime fecha;
}
