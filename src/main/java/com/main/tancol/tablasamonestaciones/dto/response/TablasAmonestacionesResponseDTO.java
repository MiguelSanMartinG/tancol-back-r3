package com.main.tancol.tablasamonestaciones.dto.response;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Value
@Builder
public class TablasAmonestacionesResponseDTO {
    UUID id;
    Integer cantidadDesde;
    Integer cantidadHasta;
    UUID productoId;
    String productoNombre;
    String productoAlias;
    BigDecimal descuento;
    String textoDescuento;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    boolean enabled;
}
