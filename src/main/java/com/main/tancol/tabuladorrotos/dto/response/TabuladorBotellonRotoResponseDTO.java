package com.main.tancol.tabuladorrotos.dto.response;

import com.main.tancol.tabuladorrotos.enums.PeriodoTabuladorRoto;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Value
@Builder
public class TabuladorBotellonRotoResponseDTO {
    UUID id;
    UUID empleadoId;
    String empleadoNombre;
    PeriodoTabuladorRoto periodo;
    BigDecimal tolerancia;
    UUID productoId;
    String productoNombre;
    UUID unidadMedidaId;
    String unidadMedidaNombre;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    boolean enabled;
}
