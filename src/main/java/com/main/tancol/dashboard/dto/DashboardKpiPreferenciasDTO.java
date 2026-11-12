package com.main.tancol.dashboard.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Preferencias de pantalla del dashboard. Las claves que el frontend no envía quedan null
 * y el servicio las completa con el valor por defecto. Las claves desconocidas se ignoran.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DashboardKpiPreferenciasDTO {

    private Boolean inlineKpis;
    private Kpis kpis;
    private Charts charts;
    private Official official;
    private Views views;
    private Colors colors;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Kpis {
        private Boolean unidadesVendidas;
        private Boolean empleadosActivos;
        private Boolean clientesAtendidos;
        private Boolean diasOperacion;
        private Boolean importeReportado;
        private Boolean promedioDiario;
        private Boolean promedioPorEmpleado;
        private Boolean operaciones;
        private Boolean ticketPromedio;
        private Boolean descuentos;
        private Boolean canceladas;
        private Boolean facturadas;
        private Boolean mejorDia;
        private Boolean peorDia;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Charts {
        private Boolean ritmoVentas;
        private Boolean topEmpleados;
        private Boolean topProductos;
        private Boolean topClientes;
        private Boolean metodosPago;
        private Boolean ventasPorRuta;
        private Boolean ventasPorConcepto;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Official {
        private Boolean consumoClientesPorDia;
        private Boolean topClientesPgExp;
        private Boolean clientesMasEquipos;
        private Boolean clientesSinConsumo;
        private Boolean empleadosMetasCumplidas;
        private Boolean topDepartamentosInsumos;
        private Boolean topInsumosComprados;
        private Boolean devolucionesEquipos;
        private Boolean mayorMovimientoEquipos;
        private Boolean menorMovimientoEquipos;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Views {
        private String ritmoVentas;
        private String topEmpleados;
        private String topProductos;
        private String topClientes;
        private String metodosPago;
        private String ventasPorRuta;
        private String ventasPorConcepto;
        private String consumoClientesPorDia;
        private String topClientesPgExp;
        private String clientesMasEquipos;
        private String clientesSinConsumo;
        private String empleadosMetasCumplidas;
        private String topDepartamentosInsumos;
        private String topInsumosComprados;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Colors {
        private String unidadesVendidas;
        private String empleadosActivos;
        private String clientesAtendidos;
        private String diasOperacion;
        private String importeReportado;
        private String promedioDiario;
        private String promedioPorEmpleado;
        private String operaciones;
        private String ticketPromedio;
        private String descuentos;
        private String canceladas;
        private String facturadas;
        private String mejorDia;
        private String peorDia;
        private String devolucionesEquipos;
        private String mayorMovimientoEquipos;
        private String menorMovimientoEquipos;
    }
}
