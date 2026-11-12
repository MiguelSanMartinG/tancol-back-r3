package com.main.tancol.logistica.enums;

public enum TipoProceso {
    MANUAL,
    AUTOMATICO,
    MANUAL_1,
    MANUAL_2,
    TORRES,
    SIN_PROCESO,
    AUTOSERVICIO;

    public static TipoProceso fromTipoProceso(String tipoProceso) {
        if (tipoProceso == null || tipoProceso.isBlank()) {
            return null;
        }
        try {
            return TipoProceso.valueOf(tipoProceso.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static TipoProceso fromTipoProcesoOrSinProceso(String tipoProceso) {
        TipoProceso parsed = fromTipoProceso(tipoProceso);
        return parsed != null ? parsed : SIN_PROCESO;
    }
}
