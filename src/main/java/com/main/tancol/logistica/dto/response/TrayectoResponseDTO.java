package com.main.tancol.logistica.dto.response;

import com.main.tancol.empleados.dto.response.EmpleadosResponseDTO;
import com.main.tancol.logistica.entities.bitacora.TrayectoEntity;
import com.main.tancol.logistica.enums.EstadoVentaRuta;
import com.main.tancol.logistica.enums.TipoProceso;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * DTO for {@link TrayectoEntity}
 */
@AllArgsConstructor
@Getter
@Builder
public class TrayectoResponseDTO implements Serializable {
    private final UUID idPartida;
    private final RutasResponseDTO ruta;
    private final EmpleadosResponseDTO conductor;
    private final VehiculosResponseDTO vehiculo;
    private final String idImagenFirma;
    private final EstadoVentaRuta estadoVentaRuta;
    private final Boolean mandadoALiquidacion;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final LocalDateTime fechaFirma;
    private final String comentario;
    private final TipoProceso tipoProceso;
    private final Set<VentaTrayectoResponseDTO> ventaTrayecto;
}