package com.main.tancol.cuadreinventario.service;

import com.main.tancol.almacen.entities.AlmacenEntity;
import com.main.tancol.common.exceptions.exeption.GenericExeption;
import com.main.tancol.cuadreinventario.dto.CuadreInventarioDTOs.CapturaPartida;
import com.main.tancol.cuadreinventario.dto.CuadreInventarioDTOs.Response;
import com.main.tancol.cuadreinventario.entities.CuadreInventarioEntity;
import com.main.tancol.cuadreinventario.entities.CuadreInventarioPartidaEntity;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CuadreInventarioServiceTest {

    private static CuadreInventarioEntity cuadreConDosPartidas() {
        AlmacenEntity almacen = new AlmacenEntity();
        almacen.setId(UUID.randomUUID());
        almacen.setNombre("Central");
        CuadreInventarioEntity cuadre = CuadreInventarioEntity.builder().almacen(almacen).build();
        cuadre.getPartidas().add(CuadreInventarioPartidaEntity.builder().id(UUID.randomUUID()).cuadre(cuadre)
                .producto("Garrafón").cantidadSistema(new BigDecimal("10")).build());
        cuadre.getPartidas().add(CuadreInventarioPartidaEntity.builder().id(UUID.randomUUID()).cuadre(cuadre)
                .producto("Tapa").cantidadSistema(new BigDecimal("5")).build());
        return cuadre;
    }

    private static CapturaPartida captura(UUID id, String cantidad) {
        CapturaPartida c = new CapturaPartida();
        c.setIdPartida(id);
        c.setCantidadFisica(cantidad == null ? null : new BigDecimal(cantidad));
        return c;
    }

    @Test
    void captura_calculaDiferenciaPositivaYNegativa() {
        CuadreInventarioEntity cuadre = cuadreConDosPartidas();
        UUID a = cuadre.getPartidas().get(0).getId();
        UUID b = cuadre.getPartidas().get(1).getId();
        CuadreInventarioService.aplicarCaptura(cuadre, List.of(captura(a, "12"), captura(b, "3")), "  ");

        assertEquals(new BigDecimal("2"), cuadre.getPartidas().get(0).getDiferencia());
        assertEquals(new BigDecimal("-2"), cuadre.getPartidas().get(1).getDiferencia());
        assertNull(cuadre.getObservaciones());

        Response r = CuadreInventarioService.toResponse(cuadre, true);
        assertEquals(2, r.getPartidasCapturadas());
        assertEquals(2, r.getPartidasConDiferencia());
        assertEquals(2, r.getPartidas().size());
    }

    @Test
    void captura_sinCantidad_quedaPendiente_y_sinDiferencia() {
        CuadreInventarioEntity cuadre = cuadreConDosPartidas();
        UUID a = cuadre.getPartidas().get(0).getId();
        CuadreInventarioService.aplicarCaptura(cuadre, List.of(captura(a, "10")), "Conteo de la mañana");
        Response r = CuadreInventarioService.toResponse(cuadre, false);
        assertEquals(1, r.getPartidasCapturadas());
        assertEquals(0, r.getPartidasConDiferencia());
        assertNull(r.getPartidas());
        assertEquals("Conteo de la mañana", cuadre.getObservaciones());
    }

    @Test
    void captura_negativa_o_de_otro_cuadre_falla() {
        CuadreInventarioEntity cuadre = cuadreConDosPartidas();
        UUID a = cuadre.getPartidas().get(0).getId();
        assertThrows(GenericExeption.class,
                () -> CuadreInventarioService.aplicarCaptura(cuadre, List.of(captura(a, "-1")), null));
        assertThrows(GenericExeption.class,
                () -> CuadreInventarioService.aplicarCaptura(cuadre, List.of(captura(UUID.randomUUID(), "1")), null));
    }
}
