package com.main.tancol.producto.dto.response.insumos;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.util.UUID;

@Value
@Builder
public class CategoriaExistenciaProductoResponseDTO {
    UUID idProducto;
    String nombre;
    BigDecimal cantidad;
    String unidad;
}
