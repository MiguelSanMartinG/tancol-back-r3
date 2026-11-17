package com.main.tancol.medidores.service;

import com.main.tancol.common.exceptions.exeption.GenericExeption;
import com.main.tancol.medidores.entities.MedidoresEntity;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MedidoresServiceReemplazoTest {

    private static MedidoresEntity medidor(String nombre, String medicionM3) {
        MedidoresEntity m = new MedidoresEntity();
        m.setIdMedidor(UUID.randomUUID());
        m.setNombre(nombre);
        m.setEnabled(true);
        m.setMedicion(medicionM3 == null ? null : new BigDecimal(medicionM3));
        m.setMedidoresHijos(new LinkedHashSet<>());
        return m;
    }

    @Test
    void reemplazoValido_noFalla() {
        assertDoesNotThrow(() -> MedidoresService.validarReemplazo(
                medidor("Pozo 1", "5.00"), medidor("Pozo 1B", "0"), new BigDecimal("5.25")));
    }

    @Test
    void mismoMedidor_falla() {
        MedidoresEntity a = medidor("Pozo 1", "5");
        assertThrows(GenericExeption.class, () -> MedidoresService.validarReemplazo(a, a, new BigDecimal("6")));
    }

    @Test
    void medicionMenorQueLaUltima_falla() {
        GenericExeption ex = assertThrows(GenericExeption.class, () -> MedidoresService.validarReemplazo(
                medidor("Pozo 1", "5.50"), medidor("Pozo 1B", "0"), new BigDecimal("5.00")));
        assertTrue(ex.getMessage().contains("5500 LTS"));
    }

    @Test
    void reemplazoEnElArbol_oYaReemplazado_falla() {
        MedidoresEntity a = medidor("Pozo 1", "1");
        MedidoresEntity conPadre = medidor("Hijo", "0");
        conPadre.setMedidorPadre(medidor("Padre", "0"));
        assertThrows(GenericExeption.class, () -> MedidoresService.validarReemplazo(a, conPadre, new BigDecimal("1")));

        MedidoresEntity yaReemplazado = medidor("Pozo 1", "1");
        yaReemplazado.setMedidorReemplazadoPor(medidor("Pozo 1B", "0"));
        assertThrows(GenericExeption.class, () -> MedidoresService.validarReemplazo(
                yaReemplazado, medidor("Pozo 1C", "0"), new BigDecimal("1")));

        MedidoresEntity inactivo = medidor("Pozo 1D", "0");
        inactivo.setEnabled(false);
        assertThrows(GenericExeption.class, () -> MedidoresService.validarReemplazo(a, inactivo, new BigDecimal("1")));
    }
}
