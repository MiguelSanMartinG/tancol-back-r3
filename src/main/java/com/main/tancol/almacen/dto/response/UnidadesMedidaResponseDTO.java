package com.main.tancol.almacen.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Builder
public class UnidadesMedidaResponseDTO {
    private UUID idUnidadDeMedida;
    // Agrega m??s campos aqu??
    String nombre;
    String alias;
    LocalDateTime updatedAt;
    LocalDateTime createdAt;
}
