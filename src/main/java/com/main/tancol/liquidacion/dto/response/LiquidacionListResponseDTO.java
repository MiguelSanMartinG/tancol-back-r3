package com.main.tancol.liquidacion.dto.response;

import com.main.tancol.liquidacion.enums.EstadosLiquidacion;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Fila ligera para la tabla de liquidaciones (GET /liquidacion/page).
 * Solo trae lo que la lista muestra; el detalle completo sigue en GET /liquidacion/{id}.
 */
public record LiquidacionListResponseDTO(
        UUID idLiquidacion,
        String folio,
        LocalDateTime fecha,
        RutaResumen ruta,
        ChoferResumen chofer,
        String bloqueNota,
        long numeroPartidas,
        BigDecimal totalEstimado,
        EstadosLiquidacion estadosLiquidacion,
        boolean origenBitacora,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public record RutaResumen(UUID id, String nombre, String numero) {
    }

    public record ChoferResumen(
            UUID idEmpleado,
            String nombre,
            String apellidoPaterno,
            String apellidoMaterno,
            String nombreUsuario
    ) {
    }
}
