package com.main.tancol.liquidacion.util;

import com.main.tancol.common.exceptions.exeption.GenericExeption;
import com.main.tancol.logistica.entities.rutas.RutasEntity;

import java.text.Normalizer;
import java.util.Locale;
import java.util.Set;

/**
 * Turno de una liquidación de autoservicio. Una ruta es de autoservicio si su tipo empieza con AUTO
 * o su nombre dice «Autoservicio». En otras rutas el turno no aplica y se guarda vacío.
 */
public final class TurnoLiquidacion {

    public static final Set<String> TURNOS = Set.of("MATUTINO", "VESPERTINO", "DOMINICAL");

    private TurnoLiquidacion() {
    }

    public static boolean esAutoservicio(RutasEntity ruta) {
        if (ruta == null) return false;
        if (ruta.getTipoRuta() != null && ruta.getTipoRuta().name().startsWith("AUTO")) return true;
        String nombre = ruta.getNombre() == null ? "" : Normalizer.normalize(ruta.getNombre(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "").toLowerCase(Locale.ROOT);
        return nombre.contains("autoservicio");
    }

    /** Valida y normaliza el turno; null si la ruta no es de autoservicio o no se capturó. */
    public static String normalizar(String turno, RutasEntity ruta) {
        if (!esAutoservicio(ruta) || turno == null || turno.isBlank()) return null;
        String valor = turno.trim().toUpperCase(Locale.ROOT);
        if (!TURNOS.contains(valor)) {
            throw new GenericExeption("El turno debe ser Matutino, Vespertino o Dominical.");
        }
        return valor;
    }
}
