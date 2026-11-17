package com.main.tancol.empleados.service;

import com.main.tancol.common.exceptions.exeption.GenericExeption;
import com.main.tancol.empleados.dto.request.EmpleadosRequestDTO;
import com.main.tancol.empleados.entities.EmpleadosEntity;
import com.main.tancol.empleados.enums.TipoContratacion;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class EmpleadoDatosLaboralesTest {

    private static EmpleadosRequestDTO.EmpleadosRequestDTOBuilder base() {
        return EmpleadosRequestDTO.builder().nombre("Ana").apellidoPaterno("López").apellidoMaterno("Ruiz");
    }

    @Test
    void temporalSinVigencia_falla() {
        EmpleadosEntity e = new EmpleadosEntity();
        GenericExeption ex = assertThrows(GenericExeption.class,
                () -> EmpleadoDatosLaborales.aplicar(e, base().tipoContratacion(TipoContratacion.TEMPORAL).build()));
        assertTrue(ex.getMessage().contains("vigencia"));
    }

    @Test
    void dePlanta_borraLaVigencia_yNormaliza() {
        EmpleadosEntity e = new EmpleadosEntity();
        EmpleadoDatosLaborales.aplicar(e, base()
                .tipoContratacion(TipoContratacion.PLANTA)
                .fechaVigencia(LocalDate.of(2026, 12, 1))
                .rfc(" lora800101ab1 ")
                .curp("lora800101mdfpzn09")
                .nss("12345678901")
                .observaciones("   ")
                .build());
        assertNull(e.getFechaVigencia());
        assertEquals("LORA800101AB1", e.getRfc());
        assertEquals("LORA800101MDFPZN09", e.getCurp());
        assertNull(e.getObservaciones());
    }

    @Test
    void formatosInvalidos_y_egresoAntesDeIngreso_fallan() {
        assertThrows(GenericExeption.class, () -> EmpleadoDatosLaborales.aplicar(new EmpleadosEntity(), base().nss("123").build()));
        assertThrows(GenericExeption.class, () -> EmpleadoDatosLaborales.aplicar(new EmpleadosEntity(), base().rfc("XX").build()));
        assertThrows(GenericExeption.class, () -> EmpleadoDatosLaborales.aplicar(new EmpleadosEntity(), base().curp("ABC").build()));
        assertThrows(GenericExeption.class, () -> EmpleadoDatosLaborales.aplicar(new EmpleadosEntity(), base()
                .fechaIngreso(LocalDate.of(2026, 5, 1)).fechaEgreso(LocalDate.of(2026, 4, 1)).build()));
    }

    @Test
    void practicanteConVigencia_seGuarda() {
        EmpleadosEntity e = new EmpleadosEntity();
        EmpleadoDatosLaborales.aplicar(e, base().tipoContratacion(TipoContratacion.PRACTICANTE)
                .fechaVigencia(LocalDate.of(2026, 11, 18)).build());
        assertEquals(TipoContratacion.PRACTICANTE, e.getTipoContratacion());
        assertEquals(LocalDate.of(2026, 11, 18), e.getFechaVigencia());
    }

    @Test
    void mensajeDeVigencia() {
        LocalDate hoy = LocalDate.of(2026, 11, 16);
        assertEquals("Ana López (Temporal) termina su vigencia en 2 días, el 18/11/2026.",
                EmpleadoVigenciaService.mensaje("Ana López", TipoContratacion.TEMPORAL, LocalDate.of(2026, 11, 18), hoy));
        assertTrue(EmpleadoVigenciaService.mensaje("A", TipoContratacion.PRACTICANTE, hoy, hoy).contains("hoy"));
    }
}
