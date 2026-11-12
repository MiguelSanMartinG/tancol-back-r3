package com.main.tancol.medidores.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.util.UUID;
@Value
public class ResetMedidorRequestDTO {
    @NotNull
    UUID idMedidor;
    String mensaje;

    // getters y setters
}