package com.main.tancol.tablascomisiones.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Builder
public class TablasComisionesResponseDTO {
    private UUID id;
    private Integer ventaMinima;
    private Integer cantidadDesde;
    private Integer cantidadHasta;
    private UUID productoId;
    private String productoNombre;
    private String productoAlias;
    private BigDecimal comision;
    private String textoComision;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean enabled;
}
