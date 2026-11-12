package com.main.tancol.dashboard.config;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DashboardKpiPropertiesTest {

    private static final UUID PG = UUID.fromString("b6ccdf91-eb42-422a-b30c-bd0a028738fc");
    private static final UUID EXP = UUID.fromString("3e397f5b-26f4-46b6-ab13-0f61b15108d0");
    private static final UUID PGNP = UUID.fromString("8a0c4c7b-351b-4391-bbea-72ffbc1aa2c1");

    @Test
    void ignoraConceptosNoOficialesEnviadosPorFrontend() {
        DashboardKpiProperties properties = new DashboardKpiProperties();
        List<UUID> solicitados = List.of(PGNP, PG, UUID.randomUUID());

        List<UUID> oficiales = properties.forzarConceptosOficiales(solicitados);

        assertEquals(2, oficiales.size());
        assertTrue(oficiales.contains(PG));
        assertTrue(oficiales.contains(EXP));
        assertFalse(oficiales.contains(PGNP));
        assertFalse(oficiales.contains(solicitados.get(2)));
    }

    @Test
    void identificaPgYExpPorUuid() {
        DashboardKpiProperties properties = new DashboardKpiProperties();

        assertTrue(properties.esPg(PG));
        assertTrue(properties.esExp(EXP));
        assertFalse(properties.esPg(EXP));
        assertFalse(properties.esExp(PGNP));
    }
}
