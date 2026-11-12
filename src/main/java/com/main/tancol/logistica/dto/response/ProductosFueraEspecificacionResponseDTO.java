package com.main.tancol.logistica.dto.response;

import com.main.tancol.logistica.entities.bitacora.comisiones.ProductosFueraEspecificacionEntity;
import com.main.tancol.producto.dto.response.ConceptosResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.util.UUID;

@AllArgsConstructor
@Builder
/**
 * DTO for {@link ProductosFueraEspecificacionEntity}
 */
@Value
public class ProductosFueraEspecificacionResponseDTO {
    UUID idPFE;
    ConceptosResponseDTO concepto;
    BigDecimal cantidad;
//    LocalDateTime createdAt;
//    LocalDateTime updatedAt;
    // Agrega m??s campos aqu??
}