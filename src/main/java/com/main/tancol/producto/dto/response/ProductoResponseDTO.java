package com.main.tancol.producto.dto.response;

import com.main.tancol.producto.entities.ProductoEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for {@link ProductoEntity}
 */
@Value
@Builder
public class ProductoResponseDTO implements Serializable {
    UUID id;
    @NotNull(message = "Debe tener un nombre")
    String nombre;
    String alias;
    @NotBlank
    String descripcion;
    @NotBlank
    String sku;
    String nombreCategoria;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

}