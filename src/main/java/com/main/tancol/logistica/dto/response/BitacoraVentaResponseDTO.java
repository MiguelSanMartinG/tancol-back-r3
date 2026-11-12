package com.main.tancol.logistica.dto.response;

import com.main.tancol.empleados.dto.response.EmpleadosBasicResponseDTO;
import com.main.tancol.logistica.enums.EstatusBitacoraVenta;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Builder
public class BitacoraVentaResponseDTO {
    private UUID idBitacora;
    private EstatusBitacoraVenta status;
    private String folio;
    private LocalDateTime fecha;
    private EmpleadosBasicResponseDTO usuario;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Set<TrayectoResponseDTO> bitacoraPartidaVentas;
    // Agrega m??s campos aqu??
}
