package com.main.tancol.prestamo.dto.request;

import com.main.tancol.prestamo.enums.PeriodoCoberturaEquipo;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Value
public class EquipoAsignacionRequestDTO {
    @NotNull
    UUID idEquipo;

    @NotNull
    UUID idCliente;

    LocalDateTime fechaAsignacion;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    BigDecimal montoMinimo;

    PeriodoCoberturaEquipo periodoCobertura;
}
