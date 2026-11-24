package com.main.tancol.inventariodiario.enums;

/** SOLICITADO → ACEPTADO | RECHAZADO → RECIBIDO; CANCELADO solo desde SOLICITADO. */
public enum EstatusSolicitud {
    SOLICITADO,
    ACEPTADO,
    RECHAZADO,
    RECIBIDO,
    CANCELADO;

    public boolean abierta() {
        return this == SOLICITADO || this == ACEPTADO;
    }
}
