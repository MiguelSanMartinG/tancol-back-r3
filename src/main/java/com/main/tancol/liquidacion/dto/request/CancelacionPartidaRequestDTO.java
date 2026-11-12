package com.main.tancol.liquidacion.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Value;

@Value
public class CancelacionPartidaRequestDTO {

    @NotBlank(message = "El motivo de cancelación es obligatorio")
    String motivo;

    @Valid
    LiquidacionPartidaRequestDTO nuevaPartida;
}
