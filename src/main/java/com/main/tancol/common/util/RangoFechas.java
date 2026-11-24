package com.main.tancol.common.util;

import com.main.tancol.common.exceptions.exeption.RangoFechasInvalidoException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

/**
 * Rango de fechas inclusivo en ambos extremos, en hora local (America/Mexico_City). Un extremo nulo queda abierto.
 * <ul>
 *     <li>Fecha sola: el inicio es a las 00:00:00 y el fin cubre el dia completo.</li>
 *     <li>Fecha-hora: se respeta; si el fin cae en el ultimo segundo del dia se extiende hasta su ultimo microsegundo.</li>
 * </ul>
 */
public record RangoFechas(LocalDateTime inicio, LocalDateTime fin) {

    public static final String MENSAJE_ORDEN = "La fecha de inicio no puede ser posterior a la fecha de fin.";

    /** Precision de microsegundos: LocalTime.MAX (nanosegundos) se redondearia al dia siguiente en PostgreSQL. */
    private static final LocalTime FIN_DEL_DIA = LocalTime.of(23, 59, 59, 999_999_000);
    private static final LocalTime ULTIMO_SEGUNDO = LocalTime.of(23, 59, 59);

    /** Valida el orden y normaliza el fin. */
    public static RangoFechas of(LocalDateTime inicio, LocalDateTime fin) {
        LocalDateTime finInclusivo = finInclusivo(fin);
        validar(inicio, finInclusivo);
        return new RangoFechas(inicio, finInclusivo);
    }

    /**
     * Interpreta extremos de texto (yyyy-MM-dd o fecha-hora ISO); vacio significa extremo abierto.
     *
     * @throws DateTimeParseException si alguno no es una fecha valida
     */
    public static RangoFechas parse(String inicio, String fin) {
        return of(parseExtremo(inicio, false), parseExtremo(fin, true));
    }

    public static void validar(LocalDateTime inicio, LocalDateTime fin) {
        if (inicio != null && fin != null && inicio.isAfter(fin)) {
            throw new RangoFechasInvalidoException(MENSAJE_ORDEN);
        }
    }

    public static void validar(LocalDate inicio, LocalDate fin) {
        if (inicio != null && fin != null && inicio.isAfter(fin)) {
            throw new RangoFechasInvalidoException(MENSAJE_ORDEN);
        }
    }

    public static LocalDateTime inicioDelDia(LocalDate fecha) {
        return fecha.atStartOfDay();
    }

    public static LocalDateTime finDelDia(LocalDate fecha) {
        return fecha.atTime(FIN_DEL_DIA);
    }

    /** La fecha si el texto es exactamente yyyy-MM-dd valido; null en otro caso. */
    static LocalDate fechaSola(String valor) {
        try {
            return LocalDate.parse(valor);
        } catch (DateTimeParseException ex) {
            return null;
        }
    }

    private static LocalDateTime finInclusivo(LocalDateTime fin) {
        if (fin != null && fin.toLocalTime().truncatedTo(ChronoUnit.SECONDS).equals(ULTIMO_SEGUNDO)) {
            return finDelDia(fin.toLocalDate());
        }
        return fin;
    }

    private static LocalDateTime parseExtremo(String valor, boolean esFin) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        String texto = valor.trim();
        LocalDate fecha = fechaSola(texto);
        if (fecha != null) {
            return esFin ? finDelDia(fecha) : inicioDelDia(fecha);
        }
        // Se toma la hora escrita como hora local; una 'Z' u offset final se ignora, igual que en el JSON de los reportes.
        return LocalDateTime.parse(texto, DateTimeFormatter.ISO_DATE_TIME);
    }
}
