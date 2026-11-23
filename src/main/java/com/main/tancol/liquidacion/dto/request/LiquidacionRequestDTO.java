package com.main.tancol.liquidacion.dto.request;

import com.main.tancol.liquidacion.entities.LiquidacionEntity;
import com.main.tancol.liquidacion.enums.EstadosLiquidacion;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO for {@link LiquidacionEntity}
 */
@Value
public class LiquidacionRequestDTO implements Serializable {
    UUID idRuta;
    @NotNull
    LocalDateTime fecha;
    String bloqueNota;
    /** Turno (MATUTINO, VESPERTINO, DOMINICAL); solo aplica en rutas de autoservicio. */
    String turno;
    EstadosLiquidacion estado;
    String observaciones;
    List<LiquidacionPartidaRequestDTO> partida;
    List<LiquidacionTotalizadoRequestDTO> totalizados;
}