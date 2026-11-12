package com.main.tancol.prestamo.dto.response;

import com.main.tancol.cliente.dto.response.ClienteResponseBasicoDTO;
import com.main.tancol.prestamo.enums.PeriodoCoberturaEquipo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Builder
public class EquipoAsignacionResponseDTO {
    private UUID id;
    private EquipoResponseDTO equipo;
    private ClienteResponseBasicoDTO cliente;
    private LocalDateTime fechaAsignacion;
    private LocalDateTime fechaDevolucion;
    private BigDecimal montoMinimo;
    private PeriodoCoberturaEquipo periodoCobertura;
    private boolean activa;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean enabled;
}
