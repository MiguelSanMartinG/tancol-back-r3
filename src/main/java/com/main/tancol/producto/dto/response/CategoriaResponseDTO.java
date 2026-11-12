package com.main.tancol.producto.dto.response;

import com.main.tancol.producto.entities.CategoriaProductoEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO for {@link CategoriaProductoEntity}
 */
@AllArgsConstructor
@Getter
@Builder
public class CategoriaResponseDTO implements Serializable {
    private final UUID id;
    private final String nombre;
    private final Boolean enabled;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    private final List<ProductoResponseDTO> productoList;
}