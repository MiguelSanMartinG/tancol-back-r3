package com.main.tancol.common.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** Extremos de un rango en JSON: admiten fecha sola (yyyy-MM-dd) ademas de los formatos habituales de LocalDateTime. */
public abstract class RangoFechasDeserializer extends StdDeserializer<LocalDateTime> {

    protected RangoFechasDeserializer() {
        super(LocalDateTime.class);
    }

    protected abstract LocalDateTime completar(LocalDate fecha);

    @Override
    public LocalDateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        if (parser.hasToken(JsonToken.VALUE_STRING)) {
            LocalDate fecha = RangoFechas.fechaSola(parser.getText().trim());
            if (fecha != null) {
                return completar(fecha);
            }
        }
        return LocalDateTimeDeserializer.INSTANCE.deserialize(parser, context);
    }

    /** Fecha sola: 00:00:00. */
    public static class Inicio extends RangoFechasDeserializer {
        @Override
        protected LocalDateTime completar(LocalDate fecha) {
            return RangoFechas.inicioDelDia(fecha);
        }
    }

    /** Fecha sola: ultimo instante del dia. */
    public static class Fin extends RangoFechasDeserializer {
        @Override
        protected LocalDateTime completar(LocalDate fecha) {
            return RangoFechas.finDelDia(fecha);
        }
    }
}
