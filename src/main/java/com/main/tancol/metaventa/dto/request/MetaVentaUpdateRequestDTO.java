package com.main.tancol.metaventa.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.math.BigDecimal;

@Value
public class MetaVentaUpdateRequestDTO {
    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    BigDecimal cantidadMinima;
}
