package com.main.tancol.logistica.dto.request;

import lombok.Value;

import java.math.BigDecimal;

/**
 *  {@link com.main.tancol.logistica.entities.bitacora.comisiones.ProductosFueraEspecificacionEntity}
 */
@Value
public class ProductosFueraEspecificacionRequestDTO {
    // Agrega campos aqu??
    private BigDecimal cantidad;
    private Long idConcepto;
}
