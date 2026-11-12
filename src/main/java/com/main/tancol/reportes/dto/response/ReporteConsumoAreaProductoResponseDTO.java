package com.main.tancol.reportes.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReporteConsumoAreaProductoResponseDTO {
    private List<Grupo> grupos;
    private List<TotalInsumo> totalesPorInsumo;
    private List<TotalEmpleado> totalesPorEmpleado;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Grupo {
        private String area;
        private String insumo;
        private BigDecimal cantidad;
        private List<Fila> filas;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Fila {
        private LocalDateTime fecha;
        private String empleado;
        private String insumo;
        private BigDecimal cantidadSolicitada;
        private String comentarioPartida;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TotalInsumo {
        private String insumo;
        private BigDecimal cantidad;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TotalEmpleado {
        private String empleado;
        private BigDecimal cantidad;
    }
}
