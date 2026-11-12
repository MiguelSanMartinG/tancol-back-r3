package com.main.tancol.cliente.dto.response;

import lombok.Builder;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for {@link com.main.tancol.cliente.entities.TipoClienteEntity}
 */
@Value
@Builder
public class TipoClienteSimpleResponseDTO implements Serializable {
    String nombre;
    String alias;
    UUID idTipoCliente;
    boolean enabled;
}