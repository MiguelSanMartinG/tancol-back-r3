package com.main.tancol.producto.dto.response.insumos;

import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class CategoriaHijaResponseDTO {
    UUID idCategoria;
    String nombre;
}
