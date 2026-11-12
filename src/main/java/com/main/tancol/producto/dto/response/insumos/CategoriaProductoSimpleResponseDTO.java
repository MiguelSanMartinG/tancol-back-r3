package com.main.tancol.producto.dto.response.insumos;

import com.main.tancol.producto.entities.insumos.CategoriaProductoEntity;
import lombok.Builder;
import lombok.Value;

import java.io.Serializable;
import java.util.UUID;

/**
 * DTO for {@link CategoriaProductoEntity}
 */
@Value
@Builder
public class CategoriaProductoSimpleResponseDTO implements Serializable {
    boolean enabled;
    UUID idCategoria;
    String nombre;
    UUID idCategoriaPadre;
    String nombreCategoriaPadre;
}