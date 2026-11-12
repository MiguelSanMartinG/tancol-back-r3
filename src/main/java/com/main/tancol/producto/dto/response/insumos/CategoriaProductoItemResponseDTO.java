package com.main.tancol.producto.dto.response.insumos;

import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class CategoriaProductoItemResponseDTO {
    UUID idProducto;
    String nombre;
    String sku;
}
