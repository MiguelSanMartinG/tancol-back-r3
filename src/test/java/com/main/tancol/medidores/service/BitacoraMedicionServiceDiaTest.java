package com.main.tancol.medidores.service;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BitacoraMedicionServiceDiaTest {

    private static final ZoneId UTC = ZoneId.of("UTC");
    private static final ZoneId MEXICO = ZoneId.of("America/Mexico_City");
    private static final LocalDate HOY = LocalDate.of(2026, 11, 16);

    @Test
    void servidorEnMexico_mismoDiaHastaLas235959() {
        assertTrue(BitacoraMedicionService.esMismoDiaMexico(LocalDateTime.of(2026, 11, 16, 0, 0), HOY, MEXICO));
        assertTrue(BitacoraMedicionService.esMismoDiaMexico(LocalDateTime.of(2026, 11, 16, 23, 59, 59), HOY, MEXICO));
        assertFalse(BitacoraMedicionService.esMismoDiaMexico(LocalDateTime.of(2026, 11, 15, 23, 59, 59), HOY, MEXICO));
    }

    @Test
    void servidorEnUtc_seConvierteAlDiaDeMexico() {
        // 16/11 05:59 UTC = 15/11 23:59 en México -> ya no es "hoy" el 16
        assertFalse(BitacoraMedicionService.esMismoDiaMexico(LocalDateTime.of(2026, 11, 16, 5, 59), HOY, UTC));
        // 17/11 05:00 UTC = 16/11 23:00 en México -> sigue siendo el 16
        assertTrue(BitacoraMedicionService.esMismoDiaMexico(LocalDateTime.of(2026, 11, 17, 5, 0), HOY, UTC));
    }

    @Test
    void sinFecha_noEsEditable() {
        assertFalse(BitacoraMedicionService.esMismoDiaMexico(null, HOY, MEXICO));
    }
}
