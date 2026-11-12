package com.main.tancol.orden.dto.request;

import com.main.tancol.orden.enums.Prioridad;
import com.main.tancol.orden.enums.SolicitudEstatus;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Value
public class SolicitudOrdenRequestDTO {
    String titulo;
    LocalDateTime fechaCreacionSolicitud;
    Prioridad prioridad;
    UUID proveedorId;
    SolicitudEstatus solicitudEstatus;
    List<PartidaOrdenRequestDTO> partidas;

}
