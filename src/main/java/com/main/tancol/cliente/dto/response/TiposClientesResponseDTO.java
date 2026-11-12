package com.main.tancol.cliente.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Builder
public class TiposClientesResponseDTO {
    private UUID idTiposClientes;
    private String nombre;
    private String alias;
    private List<ClienteResponseDTO> clientes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean enabled;
    // Agrega m??s campos aqu??
}
