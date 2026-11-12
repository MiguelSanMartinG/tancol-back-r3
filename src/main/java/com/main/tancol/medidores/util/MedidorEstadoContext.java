package com.main.tancol.medidores.util;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@Getter
public class MedidorEstadoContext {

    public static final MedidorEstadoContext VACIO = new MedidorEstadoContext(
            Collections.emptyMap(),
            Collections.emptyMap()
    );

    private final Map<UUID, Boolean> bitacoraHoy;
    private final Map<UUID, BigDecimal> consumoHoy;

    public MedidorEstadoContext(Map<UUID, Boolean> bitacoraHoy, Map<UUID, BigDecimal> consumoHoy) {
        this.bitacoraHoy = bitacoraHoy;
        this.consumoHoy = consumoHoy;
    }

    public boolean tieneBitacoraHoy(UUID idMedidor) {
        return Boolean.TRUE.equals(bitacoraHoy.get(idMedidor));
    }

    public BigDecimal getConsumoHoy(UUID idMedidor) {
        return consumoHoy.get(idMedidor);
    }
}
