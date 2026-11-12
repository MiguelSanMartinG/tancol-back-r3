package com.main.tancol.proveedor.dto.response;

import com.main.tancol.proveedor.entity.TipoImpuestoEntity;
import lombok.Builder;
import lombok.Value;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for {@link TipoImpuestoEntity}
 */
@Value
@Builder
public class TipoImpuestoResponseDTO implements Serializable {
    UUID id;
    String nombre;
    BigDecimal porcentaje;
    boolean enabled;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}