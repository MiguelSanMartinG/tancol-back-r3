package com.main.tancol.dashboard.service;

import com.main.tancol.dashboard.dto.DashboardKpiPreferenciasDTO;
import com.main.tancol.dashboard.dto.DashboardKpiPreferenciasDTO.Charts;
import com.main.tancol.dashboard.dto.DashboardKpiPreferenciasDTO.Colors;
import com.main.tancol.dashboard.dto.DashboardKpiPreferenciasDTO.Kpis;
import com.main.tancol.dashboard.dto.DashboardKpiPreferenciasDTO.Official;
import com.main.tancol.dashboard.dto.DashboardKpiPreferenciasDTO.Views;
import com.main.tancol.dashboard.exception.ConfiguracionKpiInvalidaException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

@Component
public class DashboardKpiPreferenciasNormalizer {

    static final List<String> VISTAS = List.of(
            "bars", "line", "area", "donut", "pie", "stacked", "radar", "table", "ranking");

    static final List<String> VISTAS_METAS = List.of(
            "ranking", "bars", "line", "area", "table");

    private static final Pattern HEX = Pattern.compile("^#(?:[0-9a-fA-F]{3}|[0-9a-fA-F]{6}|[0-9a-fA-F]{8})$");

    public DashboardKpiPreferenciasDTO normalizar(DashboardKpiPreferenciasDTO entrada) {
        DashboardKpiPreferenciasDTO origen = entrada == null ? new DashboardKpiPreferenciasDTO() : entrada;
        Kpis kpis = origen.getKpis() == null ? new Kpis() : origen.getKpis();
        Charts charts = origen.getCharts() == null ? new Charts() : origen.getCharts();
        Official official = origen.getOfficial() == null ? new Official() : origen.getOfficial();
        Views views = origen.getViews() == null ? new Views() : origen.getViews();
        Colors colors = origen.getColors() == null ? new Colors() : origen.getColors();

        return DashboardKpiPreferenciasDTO.builder()
                .inlineKpis(flag(origen.getInlineKpis(), false))
                .kpis(Kpis.builder()
                        .unidadesVendidas(flag(kpis.getUnidadesVendidas(), true))
                        .empleadosActivos(flag(kpis.getEmpleadosActivos(), true))
                        .clientesAtendidos(flag(kpis.getClientesAtendidos(), true))
                        .diasOperacion(flag(kpis.getDiasOperacion(), true))
                        .importeReportado(flag(kpis.getImporteReportado(), true))
                        .promedioDiario(flag(kpis.getPromedioDiario(), true))
                        .promedioPorEmpleado(flag(kpis.getPromedioPorEmpleado(), true))
                        .operaciones(flag(kpis.getOperaciones(), true))
                        .ticketPromedio(flag(kpis.getTicketPromedio(), true))
                        .descuentos(flag(kpis.getDescuentos(), true))
                        .canceladas(flag(kpis.getCanceladas(), true))
                        .facturadas(flag(kpis.getFacturadas(), true))
                        .mejorDia(flag(kpis.getMejorDia(), true))
                        .peorDia(flag(kpis.getPeorDia(), true))
                        .build())
                .charts(Charts.builder()
                        .ritmoVentas(flag(charts.getRitmoVentas(), true))
                        .topEmpleados(flag(charts.getTopEmpleados(), true))
                        .topProductos(flag(charts.getTopProductos(), true))
                        .topClientes(flag(charts.getTopClientes(), true))
                        .metodosPago(flag(charts.getMetodosPago(), true))
                        .ventasPorRuta(flag(charts.getVentasPorRuta(), true))
                        .ventasPorConcepto(flag(charts.getVentasPorConcepto(), true))
                        .build())
                .official(Official.builder()
                        .consumoClientesPorDia(flag(official.getConsumoClientesPorDia(), true))
                        .topClientesPgExp(flag(official.getTopClientesPgExp(), true))
                        .clientesMasEquipos(flag(official.getClientesMasEquipos(), true))
                        .clientesSinConsumo(flag(official.getClientesSinConsumo(), true))
                        .empleadosMetasCumplidas(flag(official.getEmpleadosMetasCumplidas(), true))
                        .topDepartamentosInsumos(flag(official.getTopDepartamentosInsumos(), true))
                        .topInsumosComprados(flag(official.getTopInsumosComprados(), true))
                        .devolucionesEquipos(flag(official.getDevolucionesEquipos(), true))
                        .mayorMovimientoEquipos(flag(official.getMayorMovimientoEquipos(), true))
                        .menorMovimientoEquipos(flag(official.getMenorMovimientoEquipos(), true))
                        .build())
                .views(Views.builder()
                        .ritmoVentas(vista(views.getRitmoVentas(), "ritmoVentas", "bars", VISTAS))
                        .topEmpleados(vista(views.getTopEmpleados(), "topEmpleados", "ranking", VISTAS))
                        .topProductos(vista(views.getTopProductos(), "topProductos", "ranking", VISTAS))
                        .topClientes(vista(views.getTopClientes(), "topClientes", "ranking", VISTAS))
                        .metodosPago(vista(views.getMetodosPago(), "metodosPago", "ranking", VISTAS))
                        .ventasPorRuta(vista(views.getVentasPorRuta(), "ventasPorRuta", "ranking", VISTAS))
                        .ventasPorConcepto(vista(views.getVentasPorConcepto(), "ventasPorConcepto", "ranking", VISTAS))
                        .consumoClientesPorDia(vista(views.getConsumoClientesPorDia(), "consumoClientesPorDia", "bars", VISTAS))
                        .topClientesPgExp(vista(views.getTopClientesPgExp(), "topClientesPgExp", "ranking", VISTAS))
                        .clientesMasEquipos(vista(views.getClientesMasEquipos(), "clientesMasEquipos", "bars", VISTAS))
                        .clientesSinConsumo(vista(views.getClientesSinConsumo(), "clientesSinConsumo", "ranking", VISTAS))
                        .empleadosMetasCumplidas(vista(views.getEmpleadosMetasCumplidas(), "empleadosMetasCumplidas", "ranking", VISTAS_METAS))
                        .topDepartamentosInsumos(vista(views.getTopDepartamentosInsumos(), "topDepartamentosInsumos", "bars", VISTAS))
                        .topInsumosComprados(vista(views.getTopInsumosComprados(), "topInsumosComprados", "bars", VISTAS))
                        .build())
                .colors(Colors.builder()
                        .unidadesVendidas(color(colors.getUnidadesVendidas(), "unidadesVendidas", "#5856d6"))
                        .empleadosActivos(color(colors.getEmpleadosActivos(), "empleadosActivos", "#0ea5e9"))
                        .clientesAtendidos(color(colors.getClientesAtendidos(), "clientesAtendidos", "#14b8a6"))
                        .diasOperacion(color(colors.getDiasOperacion(), "diasOperacion", "#f59e0b"))
                        .importeReportado(color(colors.getImporteReportado(), "importeReportado", "#8b5cf6"))
                        .promedioDiario(color(colors.getPromedioDiario(), "promedioDiario", "#06b6d4"))
                        .promedioPorEmpleado(color(colors.getPromedioPorEmpleado(), "promedioPorEmpleado", "#6366f1"))
                        .operaciones(color(colors.getOperaciones(), "operaciones", "#64748b"))
                        .ticketPromedio(color(colors.getTicketPromedio(), "ticketPromedio", "#10b981"))
                        .descuentos(color(colors.getDescuentos(), "descuentos", "#f97316"))
                        .canceladas(color(colors.getCanceladas(), "canceladas", "#ef4444"))
                        .facturadas(color(colors.getFacturadas(), "facturadas", "#22c55e"))
                        .mejorDia(color(colors.getMejorDia(), "mejorDia", "#84cc16"))
                        .peorDia(color(colors.getPeorDia(), "peorDia", "#f43f5e"))
                        .devolucionesEquipos(color(colors.getDevolucionesEquipos(), "devolucionesEquipos", "#ef4444"))
                        .mayorMovimientoEquipos(color(colors.getMayorMovimientoEquipos(), "mayorMovimientoEquipos", "#14b8a6"))
                        .menorMovimientoEquipos(color(colors.getMenorMovimientoEquipos(), "menorMovimientoEquipos", "#f59e0b"))
                        .build())
                .build();
    }

    private static boolean flag(Boolean valor, boolean porDefecto) {
        return valor == null ? porDefecto : valor;
    }

    private static String vista(String valor, String clave, String porDefecto, List<String> permitidas) {
        if (valor == null) {
            return porDefecto;
        }
        String normalizada = valor.trim().toLowerCase(Locale.ROOT);
        if (normalizada.isEmpty() || !permitidas.contains(normalizada)) {
            throw new ConfiguracionKpiInvalidaException(
                    "La vista '" + recortar(valor) + "' no es válida para " + clave
                            + ". Valores permitidos: " + String.join(", ", permitidas));
        }
        return normalizada;
    }

    private static String color(String valor, String clave, String porDefecto) {
        if (valor == null) {
            return porDefecto;
        }
        String normalizado = valor.trim().toLowerCase(Locale.ROOT);
        if (!HEX.matcher(normalizado).matches()) {
            throw new ConfiguracionKpiInvalidaException(
                    "El color de " + clave + " no es un hexadecimal válido");
        }
        return normalizado;
    }

    private static String recortar(String valor) {
        String limpio = valor.trim();
        return limpio.length() <= 32 ? limpio : limpio.substring(0, 32);
    }
}
