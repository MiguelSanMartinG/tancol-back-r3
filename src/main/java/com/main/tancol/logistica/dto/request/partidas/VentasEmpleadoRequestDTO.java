package com.main.tancol.logistica.dto.request.partidas;

import lombok.Value;

import java.math.BigDecimal;
import java.util.UUID;

@Value
public class VentasEmpleadoRequestDTO {
    // Agrega campos aqu??
    UUID idEmpleado;
    BigDecimal cantidad;
}
