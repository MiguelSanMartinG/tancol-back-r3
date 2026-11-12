package com.main.tancol.orden.dto.request;

import jakarta.validation.constraints.Positive;
import lombok.Value;

import java.math.BigDecimal;
import java.util.UUID;

@Value
public class PartidaOrdenRequestDTO {
    // Factura - Solicitud
    UUID idPartida;
    UUID idProducto;
    UUID idImpuesto;
    @Positive
    Integer cantidad;
    BigDecimal precioUnitario;
    BigDecimal descuentoAplicado;
    UUID idUnidadMedida;

}
