package com.main.tancol.almacen.dto.response;

import com.main.tancol.almacen.entities.AlmacenEntity;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for {@link AlmacenEntity}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class AlmacenResponseDTO implements Serializable {
    UUID id;
    String nombre;
    Boolean status;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

}