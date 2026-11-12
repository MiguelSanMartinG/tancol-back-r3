package com.main.tancol.dashboard.util;

import com.main.tancol.dashboard.dto.response.CantidadPorDiaResponseDTO;
import com.main.tancol.dashboard.dto.response.ConsumoClientesPorDiaResponseDTO;
import com.main.tancol.dashboard.dto.response.DiaMovimientoExtremoResponseDTO;
import com.main.tancol.dashboard.dto.response.MovimientoEquiposDiaResponseDTO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class DashboardKpiSeries {

    private DashboardKpiSeries() {
    }

    public static List<ConsumoClientesPorDiaResponseDTO> completarConsumo(
            DashboardPeriodo periodo,
            List<ConsumoClientesPorDiaResponseDTO> datos) {
        Map<LocalDate, ConsumoClientesPorDiaResponseDTO> porFecha = indexar(datos, ConsumoClientesPorDiaResponseDTO::getFecha);
        List<ConsumoClientesPorDiaResponseDTO> serie = new ArrayList<>();
        for (LocalDate dia : periodo.dias()) {
            serie.add(porFecha.getOrDefault(dia, ConsumoClientesPorDiaResponseDTO.vacio(dia)));
        }
        return serie;
    }

    public static List<CantidadPorDiaResponseDTO> completarCantidadPorDia(
            DashboardPeriodo periodo,
            List<CantidadPorDiaResponseDTO> datos) {
        Map<LocalDate, CantidadPorDiaResponseDTO> porFecha = indexar(datos, CantidadPorDiaResponseDTO::getFecha);
        List<CantidadPorDiaResponseDTO> serie = new ArrayList<>();
        for (LocalDate dia : periodo.dias()) {
            serie.add(porFecha.getOrDefault(dia, CantidadPorDiaResponseDTO.vacio(dia)));
        }
        return serie;
    }

    public static List<MovimientoEquiposDiaResponseDTO> completarMovimiento(
            DashboardPeriodo periodo,
            List<MovimientoEquiposDiaResponseDTO> datos) {
        Map<LocalDate, MovimientoEquiposDiaResponseDTO> porFecha = indexar(datos, MovimientoEquiposDiaResponseDTO::getFecha);
        List<MovimientoEquiposDiaResponseDTO> serie = new ArrayList<>();
        for (LocalDate dia : periodo.dias()) {
            serie.add(porFecha.getOrDefault(dia, MovimientoEquiposDiaResponseDTO.vacio(dia)));
        }
        return serie;
    }

    public static DiaMovimientoExtremoResponseDTO diaMayor(List<MovimientoEquiposDiaResponseDTO> serie) {
        return serie.stream()
                .filter(dia -> dia.getMovimientosTotales() > 0)
                .max(Comparator
                        .comparingInt(MovimientoEquiposDiaResponseDTO::getMovimientosTotales)
                        .thenComparing(MovimientoEquiposDiaResponseDTO::getFecha, Comparator.reverseOrder()))
                .map(DashboardKpiSeries::aExtremo)
                .orElse(null);
    }

    public static DiaMovimientoExtremoResponseDTO diaMenorConActividad(List<MovimientoEquiposDiaResponseDTO> serie) {
        return serie.stream()
                .filter(dia -> dia.getMovimientosTotales() > 0)
                .min(Comparator
                        .comparingInt(MovimientoEquiposDiaResponseDTO::getMovimientosTotales)
                        .thenComparing(MovimientoEquiposDiaResponseDTO::getFecha))
                .map(DashboardKpiSeries::aExtremo)
                .orElse(null);
    }

    private static DiaMovimientoExtremoResponseDTO aExtremo(MovimientoEquiposDiaResponseDTO dia) {
        return DiaMovimientoExtremoResponseDTO.builder()
                .fecha(dia.getFecha())
                .movimientos(dia.getMovimientosTotales())
                .build();
    }

    private static <T> Map<LocalDate, T> indexar(List<T> datos, Function<T, LocalDate> fecha) {
        return datos.stream()
                .filter(item -> fecha.apply(item) != null)
                .collect(Collectors.toMap(fecha, Function.identity(), (a, b) -> a));
    }
}
