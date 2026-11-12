package com.main.tancol.producto.dto.response.comercial;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Builder
public class CategoriaProductoComercialResponseDTO {
    private final UUID idCategoriaComercial;
    private final String nombre;
    private final Boolean enabled;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    private final List<ProductoComercialResponseDTO> productoList;
    // Agrega m??s campos aqu??
}
