package com.main.tancol.orden.dto.response;

import com.main.tancol.almacen.dto.response.AlmacenResponseDTO;
import com.main.tancol.orden.enums.Prioridad;
import com.main.tancol.orden.enums.RecepcionEstatus;
import com.main.tancol.proveedor.dto.response.ProveedorResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO for {@link com.main.tancol.orden.entities.OrdenEntity}
 */
@AllArgsConstructor
@Builder
@Getter
public class RecepcionOrdenResponseDTO implements Serializable {
    private final UUID id;
    private final String folio;
    private final Prioridad prioridad;
    private final RecepcionEstatus recepcionEstatus;
    private final ProveedorResponseDTO proveedor;
    private final String usuario;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final LocalDateTime fechaSolicitud;
    private final LocalDateTime fechaRecibo;
    private final List<PartidaOrdenResponseDTO> partidas;
}