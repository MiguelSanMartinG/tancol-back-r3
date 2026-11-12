package com.main.tancol.liquidacion.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Value;

@Value
public class CancelacionLiquidacionRequestDTO {

    @NotBlank(message = "El motivo de cancelación es obligatorio")
    String motivo;
}
