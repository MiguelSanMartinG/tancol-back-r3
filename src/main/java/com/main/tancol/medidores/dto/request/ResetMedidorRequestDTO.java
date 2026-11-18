package com.main.tancol.medidores.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Value;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Reinicio de un medidor físico: se cierra con su última lectura real (medicionAnterior, en litros
 * como la bitácora) y lo reemplaza otro medidor ya dado de alta.
 */
@Value
public class ResetMedidorRequestDTO {
    @NotNull
    UUID idMedidor;
    @NotNull
    UUID idMedidorReemplazo;
    @NotNull
    @PositiveOrZero
    BigDecimal medicionAnterior;
    String mensaje;
}
