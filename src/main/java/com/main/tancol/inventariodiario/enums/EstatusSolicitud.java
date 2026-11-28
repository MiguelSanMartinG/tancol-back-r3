package com.main.tancol.inventariodiario.enums;

/**
 * SOLICITADO → ACEPTADO | RECHAZADO → RECIBIDO | DECLINADO; CANCELADO solo desde SOLICITADO.
 * DECLINADO: el solicitante no aceptó lo entregado; no se suma al cupo.
 */
public enum EstatusSolicitud {
    SOLICITADO,
    ACEPTADO,
    RECHAZADO,
    RECIBIDO,
    CANCELADO,
    DECLINADO;

    public boolean abierta() {
        return this == SOLICITADO || this == ACEPTADO;
    }
}
