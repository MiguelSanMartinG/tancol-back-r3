package com.main.tancol.almacen.dto.response;

import com.main.tancol.almacen.entities.AlmacenEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.io.Serializable;
import java.util.UUID;

/**
 * DTO for {@link AlmacenEntity}
 */
@AllArgsConstructor
@Value
@Builder
public class AlmacenSimpleResponseDTO implements Serializable {
    boolean enabled;
    UUID idAlmacen;
    String nombre;
}