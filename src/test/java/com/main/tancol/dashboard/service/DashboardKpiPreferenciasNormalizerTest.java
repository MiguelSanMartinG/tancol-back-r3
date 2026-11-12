package com.main.tancol.dashboard.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.main.tancol.dashboard.dto.DashboardKpiConfiguracionDTO;
import com.main.tancol.dashboard.dto.DashboardKpiPreferenciasDTO;
import com.main.tancol.dashboard.dto.DashboardKpiPreferenciasDTO.Colors;
import com.main.tancol.dashboard.dto.DashboardKpiPreferenciasDTO.Kpis;
import com.main.tancol.dashboard.dto.DashboardKpiPreferenciasDTO.Views;
import com.main.tancol.dashboard.exception.ConfiguracionKpiInvalidaException;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DashboardKpiPreferenciasNormalizerTest {

    private final DashboardKpiPreferenciasNormalizer normalizer = new DashboardKpiPreferenciasNormalizer();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void sinConfiguracionDevuelveTodosLosDefaults() throws Exception {
        DashboardKpiPreferenciasDTO preferencias = normalizer.normalizar(null);

        assertCompleta(preferencias);
        assertFalse(preferencias.getInlineKpis());
        assertTrue(preferencias.getKpis().getUnidadesVendidas());
        assertTrue(preferencias.getCharts().getRitmoVentas());
        assertTrue(preferencias.getOfficial().getEmpleadosMetasCumplidas());
        assertEquals("bars", preferencias.getViews().getRitmoVentas());
        assertEquals("ranking", preferencias.getViews().getTopEmpleados());
        assertEquals("ranking", preferencias.getViews().getEmpleadosMetasCumplidas());
        assertEquals("bars", preferencias.getViews().getTopInsumosComprados());
        assertEquals("#5856d6", preferencias.getColors().getUnidadesVendidas());
        assertEquals("#f59e0b", preferencias.getColors().getMenorMovimientoEquipos());
    }

    @Test
    void conservaFalseYCompletaLoAusente() {
        Kpis kpis = new Kpis();
        kpis.setCanceladas(false);
        DashboardKpiPreferenciasDTO entrada = new DashboardKpiPreferenciasDTO();
        entrada.setInlineKpis(true);
        entrada.setKpis(kpis);

        DashboardKpiPreferenciasDTO preferencias = normalizer.normalizar(entrada);

        assertTrue(preferencias.getInlineKpis());
        assertFalse(preferencias.getKpis().getCanceladas());
        assertTrue(preferencias.getKpis().getFacturadas());
        assertEquals("bars", preferencias.getViews().getClientesMasEquipos());
        assertEquals("#ef4444", preferencias.getColors().getCanceladas());
    }

    @Test
    void normalizaVistaYColor() {
        Views views = new Views();
        views.setRitmoVentas("  LINE  ");
        views.setEmpleadosMetasCumplidas("Bars");
        Colors colors = new Colors();
        colors.setDescuentos("#F97316");
        colors.setPeorDia("#abc");
        DashboardKpiPreferenciasDTO entrada = new DashboardKpiPreferenciasDTO();
        entrada.setViews(views);
        entrada.setColors(colors);

        DashboardKpiPreferenciasDTO preferencias = normalizer.normalizar(entrada);

        assertEquals("line", preferencias.getViews().getRitmoVentas());
        assertEquals("bars", preferencias.getViews().getEmpleadosMetasCumplidas());
        assertEquals("#f97316", preferencias.getColors().getDescuentos());
        assertEquals("#abc", preferencias.getColors().getPeorDia());
    }

    @Test
    void rechazaVistaFueraDeCatalogo() {
        Views views = new Views();
        views.setRitmoVentas("burbuja");
        DashboardKpiPreferenciasDTO entrada = new DashboardKpiPreferenciasDTO();
        entrada.setViews(views);

        ConfiguracionKpiInvalidaException error = assertThrows(
                ConfiguracionKpiInvalidaException.class, () -> normalizer.normalizar(entrada));

        assertTrue(error.getMessage().contains("ritmoVentas"));
        assertTrue(error.getMessage().contains("ranking"));
    }

    @Test
    void rechazaVistaNoPermitidaEnMetas() {
        Views views = new Views();
        views.setEmpleadosMetasCumplidas("donut");
        DashboardKpiPreferenciasDTO entrada = new DashboardKpiPreferenciasDTO();
        entrada.setViews(views);

        ConfiguracionKpiInvalidaException error = assertThrows(
                ConfiguracionKpiInvalidaException.class, () -> normalizer.normalizar(entrada));

        assertTrue(error.getMessage().contains("empleadosMetasCumplidas"));
    }

    @Test
    void rechazaColorQueNoEsHex() {
        Colors colors = new Colors();
        colors.setCanceladas("red");
        DashboardKpiPreferenciasDTO entrada = new DashboardKpiPreferenciasDTO();
        entrada.setColors(colors);

        ConfiguracionKpiInvalidaException error = assertThrows(
                ConfiguracionKpiInvalidaException.class, () -> normalizer.normalizar(entrada));

        assertTrue(error.getMessage().contains("canceladas"));
    }

    @Test
    void rechazaVistaVacia() {
        Views views = new Views();
        views.setRitmoVentas("   ");
        DashboardKpiPreferenciasDTO entrada = new DashboardKpiPreferenciasDTO();
        entrada.setViews(views);

        assertThrows(ConfiguracionKpiInvalidaException.class, () -> normalizer.normalizar(entrada));
    }

    @Test
    void ignoraClavesDesconocidasYConservaLoEnviado() throws Exception {
        String json = """
                {
                  "usuarioId": "otro-usuario",
                  "configuracion": {
                    "inlineKpis": false,
                    "kpis": {
                      "unidadesVendidas": false,
                      "consulta": "select 1"
                    },
                    "views": {
                      "ritmoVentas": "area"
                    }
                  }
                }
                """;

        DashboardKpiConfiguracionDTO body = objectMapper.readValue(json, DashboardKpiConfiguracionDTO.class);
        DashboardKpiPreferenciasDTO preferencias = normalizer.normalizar(body.getConfiguracion());

        assertFalse(preferencias.getInlineKpis());
        assertFalse(preferencias.getKpis().getUnidadesVendidas());
        assertTrue(preferencias.getKpis().getEmpleadosActivos());
        assertEquals("area", preferencias.getViews().getRitmoVentas());
        assertEquals("ranking", preferencias.getViews().getTopProductos());
    }

    @Test
    void rechazaTipoIncorrecto() {
        String json = """
                {"configuracion":{"kpis":{"unidadesVendidas":"si"}}}
                """;

        assertThrows(Exception.class, () -> objectMapper.readValue(json, DashboardKpiConfiguracionDTO.class));
    }

    private static void assertCompleta(Object objeto) throws IllegalAccessException {
        for (Field field : objeto.getClass().getDeclaredFields()) {
            if (field.isSynthetic() || Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            field.setAccessible(true);
            Object valor = field.get(objeto);
            assertNotNull(valor, field.getName());
            if (!field.getType().getName().startsWith("java.")) {
                assertCompleta(valor);
            }
        }
    }
}
