package com.main.tancol.metaventa.dto.response;

import com.main.tancol.metaventa.enums.PeriodoMetaVenta;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Builder
public class MetaVentaRutaResponseDTO {
    private UUID id;
    private UUID rutaId;
    private String rutaNombre;
    private String rutaNumero;
    private UUID productoId;
    private String productoNombre;
    private String productoAlias;
    private BigDecimal cantidadMinima;
    private PeriodoMetaVenta periodo;
    private boolean activa;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaCierre;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean enabled;
}
