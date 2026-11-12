package com.main.tancol.liquidacion.dto.request;

import lombok.Value;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Value
public class LiquidacionTotalizadoEmpleadoRequestDTO {
    // Agrega campos aqu??
    UUID idEmpleado;
    BigDecimal total;
    List<LiquidacionTotalizadoEmpleadoRequestDTO> ayudantes;
}
