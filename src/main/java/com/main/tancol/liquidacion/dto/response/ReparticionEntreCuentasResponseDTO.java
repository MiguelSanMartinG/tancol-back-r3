package com.main.tancol.liquidacion.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Builder
public class ReparticionEntreCuentasResponseDTO {
    private final UUID id;
    private final LocalDateTime fecha;
    private final BigDecimal cuentaA;
    private final BigDecimal cuentaB;
    private final BigDecimal cuentaC;
    private final String usuarioQueEdito;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    // Agrega m??s campos aqu??
}
