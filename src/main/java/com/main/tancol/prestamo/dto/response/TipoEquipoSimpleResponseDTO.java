package com.main.tancol.prestamo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Builder
public class TipoEquipoSimpleResponseDTO {
    private UUID id;
    private String nombre;
    private String alias;
    private boolean enabled;
}
