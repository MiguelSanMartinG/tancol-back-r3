package com.main.tancol.dashboard.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsumoClientesPorDiaResponseDTO {
    private LocalDate fecha;
    private BigDecimal unidades;
    private int clientesAtendidos;
    private BigDecimal importe;

    public static ConsumoClientesPorDiaResponseDTO vacio(LocalDate fecha) {
        return ConsumoClientesPorDiaResponseDTO.builder()
                .fecha(fecha)
                .unidades(BigDecimal.ZERO)
                .clientesAtendidos(0)
                .importe(BigDecimal.ZERO)
                .build();
    }
}
