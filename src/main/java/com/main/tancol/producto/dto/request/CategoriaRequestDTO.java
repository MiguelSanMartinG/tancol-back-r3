package com.main.tancol.producto.dto.request;


import com.main.tancol.producto.entities.CategoriaProductoEntity;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link CategoriaProductoEntity}
 */
@Value
public class CategoriaRequestDTO implements Serializable {
    @NotNull
    String nombre;
    Boolean enabled;
}