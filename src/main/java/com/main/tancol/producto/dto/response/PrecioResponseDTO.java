package com.main.tancol.producto.dto.response;

import com.main.tancol.proveedor.dto.response.ProveedorResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for {@link com.main.tancol.producto.entities.PrecioEntity}
 */
@AllArgsConstructor
@Getter
@Builder
public class PrecioResponseDTO implements Serializable {
    private final UUID idPrecio;
    private final BigDecimal precio;
    private final ProductoResponseDTO productoEntity;
    private final ProveedorResponseDTO proveedorEntity;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}