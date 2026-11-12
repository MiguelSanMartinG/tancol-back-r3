package com.main.tancol.dashboard.dto.request;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class DashboardKpiFilter {
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private LocalDate fechaCorte;
    private int limit;
    private boolean incluirCancelados;
    private boolean incluirClientesInactivos;
    private boolean incluirNuncaConsumieron;
    private List<UUID> idRutas;
    private List<UUID> idClientes;
    private List<UUID> idTiposEquipo;
    private List<UUID> idEquipos;
    private List<UUID> conceptosVenta;
}
