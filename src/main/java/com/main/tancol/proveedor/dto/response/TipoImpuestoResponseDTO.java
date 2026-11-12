package com.main.tancol.proveedor.dto.response;

import com.main.tancol.proveedor.entity.TipoImpuesto;
import lombok.Builder;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for {@link com.main.tancol.proveedor.entity.TipoImpuesto}
 */
@Value
@Builder
public class TipoImpuestoResponseDTO implements Serializable {
    UUID id;
    String nombre;
    Double porcentaje;
    boolean enabled;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}