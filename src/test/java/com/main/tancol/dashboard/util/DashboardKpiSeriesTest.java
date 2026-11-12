package com.main.tancol.dashboard.util;

import com.main.tancol.dashboard.dto.response.ConsumoClientesPorDiaResponseDTO;
import com.main.tancol.dashboard.dto.response.MovimientoEquiposDiaResponseDTO;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class DashboardKpiSeriesTest {

    @Test
    void completarConsumoRellenaDiasEnCero() {
        DashboardPeriodo periodo = DashboardPeriodo.of(LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 3));
        List<ConsumoClientesPorDiaResponseDTO> datos = List.of(
                ConsumoClientesPorDiaResponseDTO.builder()
                        .fecha(LocalDate.of(2026, 8, 2))
                        .unidades(new BigDecimal("10"))
                        .clientesAtendidos(2)
                        .importe(new BigDecimal("100"))
                        .build()
        );

        List<ConsumoClientesPorDiaResponseDTO> serie = DashboardKpiSeries.completarConsumo(periodo, datos);

        assertEquals(3, serie.size());
        assertEquals(0, serie.get(0).getClientesAtendidos());
        assertEquals(new BigDecimal("10"), serie.get(1).getUnidades());
        assertEquals(BigDecimal.ZERO, serie.get(2).getUnidades());
    }

    @Test
    void diaMayorYMenorIgnoranDiasEnCero() {
        List<MovimientoEquiposDiaResponseDTO> serie = List.of(
                movimiento(LocalDate.of(2026, 8, 1), 0),
                movimiento(LocalDate.of(2026, 8, 2), 12),
                movimiento(LocalDate.of(2026, 8, 3), 1),
                movimiento(LocalDate.of(2026, 8, 4), 12),
                movimiento(LocalDate.of(2026, 8, 5), 0)
        );

        var mayor = DashboardKpiSeries.diaMayor(serie);
        var menor = DashboardKpiSeries.diaMenorConActividad(serie);

        assertEquals(LocalDate.of(2026, 8, 2), mayor.getFecha());
        assertEquals(12, mayor.getMovimientos());
        assertEquals(LocalDate.of(2026, 8, 3), menor.getFecha());
        assertEquals(1, menor.getMovimientos());
    }

    @Test
    void sinActividadDevuelveNulos() {
        List<MovimientoEquiposDiaResponseDTO> serie = List.of(
                movimiento(LocalDate.of(2026, 8, 1), 0),
                movimiento(LocalDate.of(2026, 8, 2), 0)
        );

        assertNull(DashboardKpiSeries.diaMayor(serie));
        assertNull(DashboardKpiSeries.diaMenorConActividad(serie));
    }

    private MovimientoEquiposDiaResponseDTO movimiento(LocalDate fecha, int total) {
        return MovimientoEquiposDiaResponseDTO.builder()
                .fecha(fecha)
                .asignaciones(total)
                .devoluciones(0)
                .movimientosTotales(total)
                .build();
    }
}
