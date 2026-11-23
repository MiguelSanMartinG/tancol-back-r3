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
    /** Turno (MATUTINO, VESPERTINO, DOMINICAL); solo en rutas de autoservicio. */
    String turno;
    String comentario;

    String idImagenFirma;
    boolean autoGenerar;

    Set<VentaTrayectoRequestDTO> ventaTrayecto;

}
