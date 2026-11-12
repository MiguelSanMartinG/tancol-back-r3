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
public class MovimientoEquiposDiaResponseDTO {
    private LocalDate fecha;
    private int asignaciones;
    private int devoluciones;
    private int movimientosTotales;

    public static MovimientoEquiposDiaResponseDTO vacio(LocalDate fecha) {
        return MovimientoEquiposDiaResponseDTO.builder()
                .fecha(fecha)
                .asignaciones(0)
                .devoluciones(0)
                .movimientosTotales(0)
                .build();
    }
}
