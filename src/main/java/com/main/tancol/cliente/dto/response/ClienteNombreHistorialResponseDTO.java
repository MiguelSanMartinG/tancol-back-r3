package com.main.tancol.cliente.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Builder
public class ClienteNombreHistorialResponseDTO {
    private final String valor;
    private final String usuario;
    private final LocalDateTime fecha;
}
