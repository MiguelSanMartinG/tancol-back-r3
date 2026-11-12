package com.main.tancol.producto.dto.request.comercial;

import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
public class CategoriaProductoComercialRequestDTO {
    // Agrega campos aqu???
    @NotNull
    String nombre;
    Boolean enabled;
}
