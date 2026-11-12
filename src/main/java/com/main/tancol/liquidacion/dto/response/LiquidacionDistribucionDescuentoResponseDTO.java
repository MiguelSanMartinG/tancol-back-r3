package com.main.tancol.liquidacion.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Builder
public class LiquidacionDistribucionDescuentoResponseDTO {
    private final UUID id;
    private final UUID idLiquidacionPartida;
    private final String tipo;
    private final BigDecimal descuento;
    private final BigDecimal cantidad;
    private final BigDecimal totalDescuento;
    private final BigDecimal cuentaA;
    private final BigDecimal cuentaB;
    private final BigDecimal cuentaC;

    // Agrega m??s campos aqu??
}
