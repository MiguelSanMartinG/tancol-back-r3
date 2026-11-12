package com.main.tancol.dashboard.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoEquiposDiarioResponseDTO {
    @Builder.Default
    private List<MovimientoEquiposDiaResponseDTO> movimientoEquiposPorDia = new ArrayList<>();
    private DiaMovimientoExtremoResponseDTO diaMayorMovimiento;
    private DiaMovimientoExtremoResponseDTO diaMenorMovimiento;
}
