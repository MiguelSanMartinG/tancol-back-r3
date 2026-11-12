package com.main.tancol.dashboard.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopDepartamentoInsumosResponseDTO {
    private String departamento;
    private BigDecimal cantidadTotal;
    @Builder.Default
    private List<Producto> productos = new ArrayList<>();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Producto {
        private UUID idProducto;
        private String producto;
        private BigDecimal cantidad;
        private String unidad;
    }
}
