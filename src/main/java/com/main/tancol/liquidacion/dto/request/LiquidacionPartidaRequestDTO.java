package com.main.tancol.liquidacion.dto.request;

import com.main.tancol.liquidacion.enums.FormaPagoLiquidacion;
import lombok.Value;

import java.math.BigDecimal;
import java.util.UUID;

@Value
public class LiquidacionPartidaRequestDTO {

    UUID idPartida;

    Long folio;
    // Agrega campos aqu??
    UUID idConceptoVenta;
    UUID idCliente;
    UUID idProducto;

    BigDecimal cantidad;
    BigDecimal precio;
    BigDecimal descuento;

    FormaPagoLiquidacion formaPago;
    Boolean factura;
}
