package com.main.tancol.producto.dto.response.insumos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Builder
public class StockMinimoItemResponseDTO {
    private UUID idProducto;
    private String nombre;
    private String sku;
    private BigDecimal stockMinimo;
    private BigDecimal stockActual;
}
