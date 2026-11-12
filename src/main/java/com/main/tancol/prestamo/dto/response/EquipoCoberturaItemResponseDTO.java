package com.main.tancol.prestamo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Builder
public class EquipoCoberturaItemResponseDTO {
    private UUID asignacionId;
    private UUID clienteId;
    private String clienteNombre;
    private UUID equipoId;
    private String equipoNombre;
    private BigDecimal montoMinimo;
    private BigDecimal montoCubierto;
    private boolean cubierto;
    private LocalDateTime periodoInicio;
    private LocalDateTime periodoFin;
}
