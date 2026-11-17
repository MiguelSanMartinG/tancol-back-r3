package com.main.tancol.logistica.dto.response;

import com.main.tancol.logistica.enums.EstatusBitacoraVenta;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Fila ligera para la tabla de bitacoras de ventas (GET /bitacoras-rutas/page).
 * El detalle con trayectos sigue en GET /bitacoras-rutas/{id}.
 */
public record BitacoraVentaListResponseDTO(
        UUID idBitacora,
        String folio,
        LocalDateTime fecha,
        EstatusBitacoraVenta status,
        long numeroPartidas,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
