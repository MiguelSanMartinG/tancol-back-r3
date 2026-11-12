package com.main.tancol.dashboard.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DashboardPeriodoTest {

    @Test
    void periodoInclusivoUsaInicioDelDiaYFinExclusivo() {
        DashboardPeriodo periodo = DashboardPeriodo.of(
                LocalDate.of(2026, 8, 1),
                LocalDate.of(2026, 8, 31));

        assertEquals(LocalDateTime.of(2026, 8, 1, 0, 0), periodo.inicio());
        assertEquals(LocalDateTime.of(2026, 9, 1, 0, 0), periodo.finExclusivo());
        assertEquals(31, periodo.dias().size());
        assertEquals(LocalDate.of(2026, 8, 1), periodo.dias().get(0));
        assertEquals(LocalDate.of(2026, 8, 31), periodo.dias().get(30));
    }

    @Test
    void unSoloDiaIncluyeFechaInicialYFinal() {
        LocalDate dia = LocalDate.of(2026, 8, 17);
        DashboardPeriodo periodo = DashboardPeriodo.of(dia, dia);

        assertEquals(dia.atStartOfDay(), periodo.inicio());
        assertEquals(dia.plusDays(1).atStartOfDay(), periodo.finExclusivo());
        assertEquals(1, periodo.dias().size());
    }

    @Test
    void rechazaFechaInicioPosteriorAFechaFin() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                DashboardPeriodo.of(LocalDate.of(2026, 8, 31), LocalDate.of(2026, 8, 1)));
        assertTrue(ex.getMessage().contains("fechaInicio"));
    }

    @Test
    void rechazaFechasNulas() {
        assertThrows(IllegalArgumentException.class, () -> DashboardPeriodo.of(null, LocalDate.of(2026, 8, 1)));
        assertThrows(IllegalArgumentException.class, () -> DashboardPeriodo.of(LocalDate.of(2026, 8, 1), null));
    }
}
