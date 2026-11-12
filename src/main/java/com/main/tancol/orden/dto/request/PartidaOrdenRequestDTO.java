package com.main.tancol.orden.dto.request;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Value
public class PartidaOrdenRequestDTO {

    UUID idProducto;
    UUID idTipoImpuesto;
    Integer cantidad;
    BigDecimal precioUnitario;
    BigDecimal descuento;

}
