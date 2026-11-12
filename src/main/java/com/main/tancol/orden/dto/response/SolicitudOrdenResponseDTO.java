package com.main.tancol.orden.dto.response;

import com.main.tancol.orden.enums.Prioridad;
import com.main.tancol.orden.enums.SolicitanteEstatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Builder
public class SolicitudOrdenResponseDTO {
    private UUID id;
    private String titulo;
    private String folio;
    private LocalDateTime fechaSolicitud;
    private LocalDateTime fechaUltimaModificacion;
    private Prioridad prioridad;
    private SolicitanteEstatus solicitanteEstatus;
    private BigDecimal totalOrden;
    private List<PartidaOrdenResponseDTO> partidas;

    // Agrega m??s campos aqu??
}
