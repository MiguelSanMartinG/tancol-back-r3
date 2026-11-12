package com.main.tancol.almacen.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
public class WhitelistConfiguracionRequestDTO {
    @NotNull(message = "Indica si la whitelist está activa")
    Boolean usarWhitelist;
}
