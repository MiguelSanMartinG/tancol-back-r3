package com.main.tancol.producto.dto.response.comercial;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Builder
public class ProductoComercialResponseBasicoDTO {
    private UUID idProductoComercial;
    private String nombre;
    private BigDecimal precio;
}
