package com.main.tancol.prestamo.dto;

import com.main.tancol.prestamo.entities.EquipoAsignacionEntity;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class EquipoCoberturaResultado {
    private EquipoAsignacionEntity asignacion;
    private LocalDateTime periodoInicio;
    private LocalDateTime periodoFin;
    private BigDecimal montoMinimo;
    private BigDecimal montoCubierto;
    private boolean cubierto;
}
