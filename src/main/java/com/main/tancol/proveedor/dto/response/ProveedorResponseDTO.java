package com.main.tancol.proveedor.dto.response;

import com.main.tancol.proveedor.entity.ProveedorEntity;
import lombok.Builder;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for {@link ProveedorEntity}
 */
@Value
@Builder
public class ProveedorResponseDTO implements Serializable {
    UUID id;
    String nombre;
    String razonSocial;
    boolean enabled;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
//    Set<ProductosProveedores> productosProveedores;
}