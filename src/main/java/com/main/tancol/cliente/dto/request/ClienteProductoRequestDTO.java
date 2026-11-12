package com.main.tancol.cliente.dto.request;

import lombok.Value;

import java.math.BigDecimal;
import java.util.UUID;

@Value
public class ClienteProductoRequestDTO {
    UUID idPrecioCliente;
    UUID idProducto;
    BigDecimal descuento;

}
