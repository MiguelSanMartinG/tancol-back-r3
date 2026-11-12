package com.main.tancol.reportes.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReporteSalidaMaterialResponseDTO {
    private List<Fila> filas;
    private List<TotalInsumo> totalesPorInsumo;
    private int totalFilas;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Fila {
        private UUID idPartida;
        private UUID idOrdenInterna;
        private String folio;
        private LocalDateTime fecha;
        private String area;
        private String estado;
        private String empleadoEntrega;
        private String empleadoRecibe;
        private UUID idAlmacen;
        private String almacen;
        private UUID idInsumo;
        private String insumo;
        private String alias;
        private String unidadMedida;
        private BigDecimal cantidad;
        private String comentarioPartida;
        private String comentarioOrden;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TotalInsumo {
        private String insumo;
        private String unidadMedida;
        private BigDecimal cantidad;
    }
}
