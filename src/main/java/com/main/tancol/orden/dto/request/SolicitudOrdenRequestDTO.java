package com.main.tancol.orden.dto.request;

import com.main.tancol.orden.enums.Prioridad;
import com.main.tancol.orden.enums.SolicitanteEstatus;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Value
public class SolicitudOrdenRequestDTO {

    UUID proveedorId;
    LocalDateTime fechaSolicitud;
    Prioridad prioridad;
    String titulo;
    SolicitanteEstatus solicitanteEstatus;
    List<PartidaOrdenRequestDTO> partidas;

}
