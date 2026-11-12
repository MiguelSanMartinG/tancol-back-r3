package com.main.tancol.cliente.dto.response;

import com.main.tancol.producto.dto.response.comercial.ProductoComercialResponseBasicoDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Builder
public class ClienteProductoResponseDTO {
    UUID idPrecio;
    ClienteResponseBasicoDTO cliente;
    ProductoComercialResponseBasicoDTO producto;
    BigDecimal descuento;

}
