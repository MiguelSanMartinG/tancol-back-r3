package com.main.tancol.proveedor.dto.response;

import jdk.jfr.Enabled;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Builder
public class TipoProveedorResponseDTO {
    private UUID id;
    private String nombre;
    private String alias;
    private List<ProveedorResponseDTO> proveedores;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean enabled;

    // Agrega m??s campos aqu??
}
