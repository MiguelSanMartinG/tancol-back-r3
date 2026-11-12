package com.main.tancol.producto.dto.response.comercial;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Builder
public class CategoriaProductoComercialSimpleResponseDTO {
    private final UUID idCategoriaComercial;
    private final String nombre;
    private final Boolean enabled;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}