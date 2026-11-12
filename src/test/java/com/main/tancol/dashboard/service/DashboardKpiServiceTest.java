package com.main.tancol.dashboard.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DashboardKpiServiceTest {

    @Test
    void limitPorDefectoEsDiez() {
        assertEquals(10, DashboardKpiService.normalizeLimit(null));
        assertEquals(10, DashboardKpiService.normalizeLimit(0));
        assertEquals(10, DashboardKpiService.normalizeLimit(-5));
    }

    @Test
    void limitSeAcotaACien() {
        assertEquals(25, DashboardKpiService.normalizeLimit(25));
        assertEquals(100, DashboardKpiService.normalizeLimit(500));
    }
}
