package com.main.tancol.metaventa.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Builder
public class MetaVentaCoberturaItemResponseDTO {
    private UUID metaId;
    private UUID empleadoId;
    private String empleadoNombre;
    private UUID productoId;
    private String productoNombre;
    private String productoAlias;
    private BigDecimal cantidadMinima;
    private BigDecimal cantidadVendida;
    private boolean cubierto;
    private LocalDateTime periodoInicio;
    private LocalDateTime periodoFin;
}
