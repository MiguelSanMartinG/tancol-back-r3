package com.main.tancol.orden.dto.response;

import com.main.tancol.orden.enums.Prioridad;
import com.main.tancol.orden.enums.SolicitudEstatus;
import com.main.tancol.proveedor.dto.response.ProveedorResponseDTO;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class SolicitudOrdenResponseDTO
{
    private UUID idSolicitud;
    private String titulo;
    private String folio;
    private BigDecimal totalOrden;

    private LocalDateTime fechaCreacionSolicitud;
    private LocalDateTime fechaModificacionSolicitud;
    private Prioridad prioridad;
    private SolicitudEstatus solicitudEstatus;

    private ProveedorResponseDTO proveedor;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<PartidaOrdenResponseDTO> partidas;
}
