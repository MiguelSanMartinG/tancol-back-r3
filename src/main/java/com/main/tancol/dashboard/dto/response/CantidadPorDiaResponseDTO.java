package com.main.tancol.dashboard.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CantidadPorDiaResponseDTO {
    private LocalDate fecha;
    private int cantidad;

    public static CantidadPorDiaResponseDTO vacio(LocalDate fecha) {
        return CantidadPorDiaResponseDTO.builder()
                .fecha(fecha)
                .cantidad(0)
                .build();
    }
}
