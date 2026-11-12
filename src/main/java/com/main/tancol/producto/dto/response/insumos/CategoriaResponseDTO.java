package com.main.tancol.producto.dto.response.insumos;

import com.main.tancol.producto.entities.insumos.CategoriaProductoEntity;
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
    private final UUID idCategoria;
    private final String nombre;
    private final Boolean enabled;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    private final UUID idCategoriaPadre;
    private final String nombreCategoriaPadre;
    private final List<CategoriaHijaResponseDTO> subcategorias;
    private final List<ProductoResponseDTO> productoList;
}