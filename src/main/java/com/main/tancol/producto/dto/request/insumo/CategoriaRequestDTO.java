package com.main.tancol.producto.dto.request.insumo;


import com.main.tancol.producto.entities.insumos.CategoriaProductoEntity;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.io.Serializable;
import java.util.UUID;

/**
 * DTO for {@link CategoriaProductoEntity}
 */
@Value
public class CategoriaRequestDTO implements Serializable {
    @NotNull
    String nombre;
    Boolean enabled;
    UUID categoriaPadreId;
}