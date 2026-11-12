package com.main.tancol.dashboard.controller;

import com.main.tancol.dashboard.dto.DashboardKpiConfiguracionDTO;
import com.main.tancol.dashboard.dto.request.DashboardKpiFilter;
import com.main.tancol.dashboard.dto.response.ClienteMasEquiposResponseDTO;
import com.main.tancol.dashboard.dto.response.ClienteSinConsumoResponseDTO;
import com.main.tancol.dashboard.dto.response.ConsumoClientesPorDiaResponseDTO;
import com.main.tancol.dashboard.dto.response.DashboardKpisOfficialResponseDTO;
import com.main.tancol.dashboard.dto.response.DevolucionesEquiposResponseDTO;
import com.main.tancol.dashboard.dto.response.MovimientoEquiposDiarioResponseDTO;
import com.main.tancol.dashboard.dto.response.TopClientePgExpResponseDTO;
import com.main.tancol.dashboard.dto.response.TopDepartamentoInsumosResponseDTO;
import com.main.tancol.dashboard.dto.response.TopInsumoCompradoResponseDTO;
import com.main.tancol.dashboard.service.DashboardKpiConfiguracionService;
import com.main.tancol.dashboard.service.DashboardKpiService;
import com.main.tancol.metaventa.dto.response.MetaVentaCumplidasResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/dashboard/kpis")
@Tag(name = "Dashboard KPIs")
@RequiredArgsConstructor
public class DashboardKpiController {

    private final DashboardKpiService dashboardKpiService;
    private final DashboardKpiConfiguracionService dashboardKpiConfiguracionService;

    @GetMapping("/configuracion")
    @Operation(summary = "Preferencias de KPIs del usuario autenticado")
    public ResponseEntity<DashboardKpiConfiguracionDTO> configuracion() {
        return ResponseEntity.ok(dashboardKpiConfiguracionService.obtener());
    }

    @PutMapping("/configuracion")
    @Operation(summary = "Guarda o reemplaza las preferencias de KPIs del usuario autenticado")
    public ResponseEntity<DashboardKpiConfiguracionDTO> guardarConfiguracion(
            @Valid @RequestBody DashboardKpiConfiguracionDTO body) {
        return ResponseEntity.ok(dashboardKpiConfiguracionService.guardar(body.getConfiguracion()));
    }

    @GetMapping("/official")
    @Operation(summary = "KPIs oficiales agregados del periodo")
    public ResponseEntity<DashboardKpisOfficialResponseDTO> official(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false, defaultValue = "false") boolean incluirCancelados,
            @RequestParam(required = false, defaultValue = "false") boolean incluirClientesInactivos,
            @RequestParam(required = false, defaultValue = "false") boolean incluirNuncaConsumieron,
            @RequestParam(required = false) List<UUID> idRutas,
            @RequestParam(required = false) List<UUID> idClientes,
            @RequestParam(required = false) List<UUID> idTiposEquipo,
            @RequestParam(required = false) List<UUID> idEquipos,
            @RequestParam(required = false) List<UUID> conceptosVenta) {
        return ResponseEntity.ok(dashboardKpiService.official(buildFilter(
                fechaInicio, fechaFin, null, limit, incluirCancelados, incluirClientesInactivos,
                incluirNuncaConsumieron, idRutas, idClientes, idTiposEquipo, idEquipos, conceptosVenta)));
    }

    @GetMapping("/consumo-clientes-diario")
    @Operation(summary = "Consumo de clientes por día")
    public ResponseEntity<List<ConsumoClientesPorDiaResponseDTO>> consumoClientesDiario(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false, defaultValue = "false") boolean incluirCancelados,
            @RequestParam(required = false, defaultValue = "false") boolean incluirClientesInactivos,
            @RequestParam(required = false) List<UUID> idRutas,
            @RequestParam(required = false) List<UUID> idClientes) {
        return ResponseEntity.ok(dashboardKpiService.consumoClientesDiario(buildFilter(
                fechaInicio, fechaFin, null, limit, incluirCancelados, incluirClientesInactivos,
                false, idRutas, idClientes, null, null, null)));
    }

    @GetMapping("/top-clientes-pg-exp")
    @Operation(summary = "Top de clientes por consumo de PG y EXP")
    public ResponseEntity<List<TopClientePgExpResponseDTO>> topClientesPgExp(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false, defaultValue = "false") boolean incluirCancelados,
            @RequestParam(required = false, defaultValue = "false") boolean incluirClientesInactivos,
            @RequestParam(required = false) List<UUID> idRutas,
            @RequestParam(required = false) List<UUID> idClientes,
            @RequestParam(required = false) List<UUID> conceptosVenta) {
        return ResponseEntity.ok(dashboardKpiService.topClientesPgExp(buildFilter(
                fechaInicio, fechaFin, null, limit, incluirCancelados, incluirClientesInactivos,
                false, idRutas, idClientes, null, null, conceptosVenta)));
    }

    @GetMapping("/top-departamentos-insumos")
    @Operation(summary = "Top de departamentos que más insumos solicitan y sus 3 productos")
    public ResponseEntity<List<TopDepartamentoInsumosResponseDTO>> topDepartamentosInsumos(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) Integer limit) {
        return ResponseEntity.ok(dashboardKpiService.topDepartamentosInsumos(buildFilter(
                fechaInicio, fechaFin, null, limit, false, false,
                false, null, null, null, null, null)));
    }

    @GetMapping("/top-insumos-comprados")
    @Operation(summary = "Top de insumos más comprados")
    public ResponseEntity<List<TopInsumoCompradoResponseDTO>> topInsumosComprados(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) Integer limit) {
        return ResponseEntity.ok(dashboardKpiService.topInsumosComprados(buildFilter(
                fechaInicio, fechaFin, null, limit, false, false,
                false, null, null, null, null, null)));
    }

    @GetMapping("/clientes-mas-equipos")
    @Operation(summary = "Clientes con más equipos asignados")
    public ResponseEntity<List<ClienteMasEquiposResponseDTO>> clientesMasEquipos(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false, defaultValue = "false") boolean incluirClientesInactivos,
            @RequestParam(required = false) List<UUID> idRutas,
            @RequestParam(required = false) List<UUID> idClientes,
            @RequestParam(required = false) List<UUID> idTiposEquipo,
            @RequestParam(required = false) List<UUID> idEquipos) {
        return ResponseEntity.ok(dashboardKpiService.clientesConMasEquipos(buildFilter(
                fechaInicio, fechaFin, fechaFin, limit, false, incluirClientesInactivos,
                false, idRutas, idClientes, idTiposEquipo, idEquipos, null)));
    }

    @GetMapping("/clientes-sin-consumo")
    @Operation(summary = "Clientes con más tiempo sin consumir")
    public ResponseEntity<List<ClienteSinConsumoResponseDTO>> clientesSinConsumo(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaCorte,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false, defaultValue = "false") boolean incluirCancelados,
            @RequestParam(required = false, defaultValue = "false") boolean incluirClientesInactivos,
            @RequestParam(required = false, defaultValue = "true") boolean incluirNuncaConsumieron,
            @RequestParam(required = false) List<UUID> idRutas,
            @RequestParam(required = false) List<UUID> idClientes) {
        LocalDate corte = fechaCorte != null ? fechaCorte : fechaFin;
        return ResponseEntity.ok(dashboardKpiService.clientesSinConsumo(buildFilter(
                null, fechaFin, corte, limit, incluirCancelados, incluirClientesInactivos,
                incluirNuncaConsumieron, idRutas, idClientes, null, null, null)));
    }

    @GetMapping("/devoluciones-equipos")
    @Operation(summary = "Devoluciones de equipos en el periodo")
    public ResponseEntity<DevolucionesEquiposResponseDTO> devolucionesEquipos(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false, defaultValue = "false") boolean incluirClientesInactivos,
            @RequestParam(required = false) List<UUID> idRutas,
            @RequestParam(required = false) List<UUID> idClientes,
            @RequestParam(required = false) List<UUID> idTiposEquipo,
            @RequestParam(required = false) List<UUID> idEquipos) {
        return ResponseEntity.ok(dashboardKpiService.devolucionesEquipos(buildFilter(
                fechaInicio, fechaFin, null, null, false, incluirClientesInactivos,
                false, idRutas, idClientes, idTiposEquipo, idEquipos, null)));
    }

    @GetMapping("/metas-empleados-cumplidas")
    @Operation(summary = "Empleados que cumplieron la meta en la última semana cerrada, por producto")
    public ResponseEntity<MetaVentaCumplidasResponseDTO> metasEmpleadosCumplidas() {
        return ResponseEntity.ok(dashboardKpiService.metasEmpleadosCumplidas());
    }

    @GetMapping("/movimiento-equipos-diario")
    @Operation(summary = "Movimiento diario de equipos y días extremo")
    public ResponseEntity<MovimientoEquiposDiarioResponseDTO> movimientoEquiposDiario(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false, defaultValue = "false") boolean incluirClientesInactivos,
            @RequestParam(required = false) List<UUID> idRutas,
            @RequestParam(required = false) List<UUID> idClientes,
            @RequestParam(required = false) List<UUID> idTiposEquipo,
            @RequestParam(required = false) List<UUID> idEquipos) {
        return ResponseEntity.ok(dashboardKpiService.movimientoEquiposDiario(buildFilter(
                fechaInicio, fechaFin, null, null, false, incluirClientesInactivos,
                false, idRutas, idClientes, idTiposEquipo, idEquipos, null)));
    }

    private DashboardKpiFilter buildFilter(LocalDate fechaInicio,
                                           LocalDate fechaFin,
                                           LocalDate fechaCorte,
                                           Integer limit,
                                           boolean incluirCancelados,
                                           boolean incluirClientesInactivos,
                                           boolean incluirNuncaConsumieron,
                                           List<UUID> idRutas,
                                           List<UUID> idClientes,
                                           List<UUID> idTiposEquipo,
                                           List<UUID> idEquipos,
                                           List<UUID> conceptosVenta) {
        return DashboardKpiFilter.builder()
                .fechaInicio(fechaInicio)
                .fechaFin(fechaFin)
                .fechaCorte(fechaCorte)
                .limit(limit == null ? 0 : limit)
                .incluirCancelados(incluirCancelados)
                .incluirClientesInactivos(incluirClientesInactivos)
                .incluirNuncaConsumieron(incluirNuncaConsumieron)
                .idRutas(idRutas)
                .idClientes(idClientes)
                .idTiposEquipo(idTiposEquipo)
                .idEquipos(idEquipos)
                .conceptosVenta(conceptosVenta)
                .build();
    }
}
