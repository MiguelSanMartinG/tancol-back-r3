package com.main.tancol.logistica.dto.request;

import lombok.Value;

import java.util.Set;
import java.util.UUID;

@Value
public class TrayectoRequestDTO {

    UUID idBitacora;

    UUID idRuta;
    UUID idChofer;
    UUID idVehiculo;
    String tipoProceso;
    String comentario;

    String idImagenFirma;
    boolean autoGenerar;

    Set<VentaTrayectoRequestDTO> ventaTrayecto;

}
