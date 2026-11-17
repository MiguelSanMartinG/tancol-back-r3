package com.main.tancol.empleados.service;

import com.main.tancol.common.exceptions.exeption.GenericExeption;
import com.main.tancol.empleados.dto.request.EmpleadosRequestDTO;
import com.main.tancol.empleados.entities.EmpleadosEntity;
import com.main.tancol.empleados.enums.TipoContratacion;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Datos laborales del empleado (NSS, RFC, CURP, fechas, tipo de contratación y vigencia).
 * Normaliza (mayúsculas, vacío = null) y valida antes de guardar; los errores responden 409.
 */
final class EmpleadoDatosLaborales {

    static final Pattern NSS = Pattern.compile("^\\d{11}$");
    static final Pattern RFC = Pattern.compile("^[A-ZÑ&]{3,4}\\d{6}[A-Z0-9]{3}$");
    static final Pattern CURP = Pattern.compile("^[A-Z]{4}\\d{6}[HM][A-Z]{5}[A-Z0-9]\\d$");
    static final int MAX_OBSERVACIONES = 1000;

    private EmpleadoDatosLaborales() {
    }

    static void aplicar(EmpleadosEntity empleado, EmpleadosRequestDTO dto) {
        String nss = limpiar(dto.getNss());
        String rfc = mayusculas(dto.getRfc());
        String curp = mayusculas(dto.getCurp());
        validarFormato(nss, NSS, "El NSS debe tener 11 dígitos.");
        validarFormato(rfc, RFC, "El RFC no tiene un formato válido.");
        validarFormato(curp, CURP, "La CURP no tiene un formato válido.");

        if (dto.getFechaIngreso() != null && dto.getFechaEgreso() != null
                && dto.getFechaEgreso().isBefore(dto.getFechaIngreso())) {
            throw new GenericExeption("La fecha de egreso no puede ser anterior a la fecha de ingreso.");
        }

        String observaciones = limpiar(dto.getObservaciones());
        if (observaciones != null && observaciones.length() > MAX_OBSERVACIONES) {
            throw new GenericExeption("Las observaciones no pueden pasar de " + MAX_OBSERVACIONES + " caracteres.");
        }

        TipoContratacion tipo = dto.getTipoContratacion();
        if (tipo != null && tipo.requiereVigencia() && dto.getFechaVigencia() == null) {
            throw new GenericExeption("La fecha de vigencia es obligatoria para un empleado "
                    + tipo.etiqueta().toLowerCase(Locale.ROOT) + ".");
        }

        empleado.setNss(nss);
        empleado.setRfc(rfc);
        empleado.setCurp(curp);
        empleado.setFechaIngreso(dto.getFechaIngreso());
        empleado.setFechaEgreso(dto.getFechaEgreso());
        empleado.setObservaciones(observaciones);
        empleado.setTipoContratacion(tipo);
        empleado.setFechaVigencia(tipo != null && tipo.requiereVigencia() ? dto.getFechaVigencia() : null);
    }

    private static void validarFormato(String valor, Pattern patron, String mensaje) {
        if (valor != null && !patron.matcher(valor).matches()) {
            throw new GenericExeption(mensaje);
        }
    }

    private static String limpiar(String valor) {
        if (valor == null) {
            return null;
        }
        String recortado = valor.trim();
        return recortado.isEmpty() ? null : recortado;
    }

    private static String mayusculas(String valor) {
        String limpio = limpiar(valor);
        return limpio == null ? null : limpio.toUpperCase(Locale.ROOT);
    }
}
