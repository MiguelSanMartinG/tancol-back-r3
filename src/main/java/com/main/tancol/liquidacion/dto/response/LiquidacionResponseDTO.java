package com.main.tancol.liquidacion.dto.response;

import com.main.tancol.liquidacion.entities.LiquidacionEntity;
import com.main.tancol.liquidacion.enums.EstadosLiquidacion;
import com.main.tancol.logistica.dto.response.RutasResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO for {@link LiquidacionEntity}
 */
@AllArgsConstructor
@Getter
@Builder
public class LiquidacionResponseDTO{
    UUID idLiquidacion;
    String folio;
    RutasResponseDTO ruta;
    LocalDateTime fecha;
    String bloqueNota;
    EstadosLiquidacion estadosLiquidacion;
    List<LiquidacionTotalizadoResponseDTO> totalizados;
    List<LiquidacionPartidaResponseDTO> partidas;
    String observaciones;
    boolean origenBitacora;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    String motivoCancelacion;
    String usuarioCancelacion;
    LocalDateTime fechaCancelacion;
}