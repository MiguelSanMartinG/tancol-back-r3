package com.main.tancol.almacen.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class WhitelistConfiguracionResponseDTO {
    private boolean usarWhitelist;
}
