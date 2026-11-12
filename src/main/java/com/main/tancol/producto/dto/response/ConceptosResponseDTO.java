package com.main.tancol.producto.dto.response;

import com.main.tancol.producto.enums.TipoRezago;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Builder
public class ConceptosResponseDTO {
    private Long id;
    private TipoRezago tipoRezago;
    private String motivo;
    private Boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    // Agrega m??s campos aqu??
}
