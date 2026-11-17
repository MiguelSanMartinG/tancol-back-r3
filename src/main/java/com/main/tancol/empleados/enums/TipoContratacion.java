package com.main.tancol.empleados.enums;

/** Tipo de contratación del empleado. Temporal y practicante llevan fecha de vigencia. */
public enum TipoContratacion {
    PLANTA,
    TEMPORAL,
    PRACTICANTE;

    public boolean requiereVigencia() {
        return this == TEMPORAL || this == PRACTICANTE;
    }

    public String etiqueta() {
        return switch (this) {
            case PLANTA -> "De planta";
            case TEMPORAL -> "Temporal";
            case PRACTICANTE -> "Practicante";
        };
    }
}
