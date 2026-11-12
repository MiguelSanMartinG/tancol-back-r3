package com.main.tancol.dashboard.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public record DashboardPeriodo(LocalDate fechaInicio, LocalDate fechaFin, LocalDateTime inicio, LocalDateTime finExclusivo) {

    public static DashboardPeriodo of(LocalDate fechaInicio, LocalDate fechaFin) {
        if (fechaInicio == null || fechaFin == null) {
            throw new IllegalArgumentException("fechaInicio y fechaFin son obligatorios");
        }
        if (fechaInicio.isAfter(fechaFin)) {
            throw new IllegalArgumentException("fechaInicio debe ser menor o igual a fechaFin");
        }
        return new DashboardPeriodo(
                fechaInicio,
                fechaFin,
                fechaInicio.atStartOfDay(),
                fechaFin.plusDays(1).atStartOfDay()
        );
    }

    public static LocalDate hoyMexico() {
        return LocalDate.now(DashboardTimeZone.ZONA_MEXICO);
    }

    public List<LocalDate> dias() {
        List<LocalDate> fechas = new ArrayList<>();
        for (LocalDate dia = fechaInicio; !dia.isAfter(fechaFin); dia = dia.plusDays(1)) {
            fechas.add(dia);
        }
        return fechas;
    }
}
