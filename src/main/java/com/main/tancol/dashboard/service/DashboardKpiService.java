package com.main.tancol.dashboard.service;

import com.main.tancol.dashboard.config.DashboardKpiProperties;
import com.main.tancol.dashboard.dto.request.DashboardKpiFilter;
import com.main.tancol.dashboard.dto.response.ClienteMasEquiposResponseDTO;
import com.main.tancol.dashboard.dto.response.ClienteSinConsumoResponseDTO;
import com.main.tancol.dashboard.dto.response.ConsumoClientesPorDiaResponseDTO;
import com.main.tancol.dashboard.dto.response.DashboardKpiMetadataResponseDTO;
import com.main.tancol.dashboard.dto.response.DashboardKpisOfficialResponseDTO;
import com.main.tancol.dashboard.dto.response.DevolucionesEquiposResponseDTO;
import com.main.tancol.dashboard.dto.response.MovimientoEquiposDiarioResponseDTO;
import com.main.tancol.dashboard.dto.response.PeriodoKpiResponseDTO;
import com.main.tancol.dashboard.dto.response.TopClientePgExpResponseDTO;
import com.main.tancol.dashboard.dto.response.TopDepartamentoInsumosResponseDTO;
import com.main.tancol.dashboard.dto.response.TopInsumoCompradoResponseDTO;
import com.main.tancol.dashboard.query.DashboardConsumoQuery;
import com.main.tancol.dashboard.query.DashboardEquipoQuery;
import com.main.tancol.dashboard.query.DashboardInsumosQuery;
import com.main.tancol.dashboard.util.DashboardKpiSeries;
import com.main.tancol.dashboard.util.DashboardPeriodo;
import com.main.tancol.dashboard.util.DashboardTimeZone;
import com.main.tancol.metaventa.dto.response.MetaVentaCumplidasResponseDTO;
import com.main.tancol.metaventa.service.MetaVentaCoberturaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardKpiService {

    static final int DEFAULT_LIMIT = 10;
    static final int MAX_LIMIT = 100;

    private final DashboardConsumoQuery consumoQuery;
    private final DashboardEquipoQuery equipoQuery;
    private final DashboardInsumosQuery insumosQuery;
    private final DashboardKpiProperties properties;
    private final MetaVentaCoberturaService metaVentaCoberturaService;

    public DashboardKpisOfficialResponseDTO official(DashboardKpiFilter filter) {
        DashboardKpiFilter normalized = normalize(filter);
        DashboardPeriodo periodo = requirePeriodo(normalized);

        List<ConsumoClientesPorDiaResponseDTO> consumo = DashboardKpiSeries.completarConsumo(
                periodo, consumoQuery.consumoPorDia(normalized, periodo));
        List<TopClientePgExpResponseDTO> top = consumoQuery.topClientesPgExp(normalized, periodo, properties);
        List<TopDepartamentoInsumosResponseDTO> topDepartamentos = insumosQuery.topDepartamentos(periodo, normalized.getLimit());
        List<TopInsumoCompradoResponseDTO> topInsumos = insumosQuery.topInsumosComprados(periodo, normalized.getLimit());
        List<ClienteMasEquiposResponseDTO> equipos = equipoQuery.clientesConMasEquipos(normalized, periodo);
        List<ClienteSinConsumoResponseDTO> sinConsumo = consumoQuery.clientesSinConsumo(normalized, normalized.getFechaCorte());
        DevolucionesEquiposResponseDTO devoluciones = completarDevoluciones(
                equipoQuery.devoluciones(normalized, periodo), periodo);
        MovimientoEquiposDiarioResponseDTO movimiento = movimientoDiario(normalized, periodo);

        return DashboardKpisOfficialResponseDTO.builder()
                .periodo(periodoResponse(periodo))
                .consumoClientesPorDia(consumo)
                .topClientesPgExp(top)
                .topDepartamentosInsumos(topDepartamentos)
                .topInsumosComprados(topInsumos)
                .clientesConMasEquipos(equipos)
                .clientesSinConsumo(sinConsumo)
                .devolucionesEquipos(devoluciones)
                .movimientoEquiposPorDia(movimiento.getMovimientoEquiposPorDia())
                .diaMayorMovimiento(movimiento.getDiaMayorMovimiento())
                .diaMenorMovimiento(movimiento.getDiaMenorMovimiento())
                .metadata(DashboardKpiMetadataResponseDTO.builder()
                        .generadoEn(Instant.now().toString())
                        .incluyeCancelados(normalized.isIncluirCancelados())
                        .build())
                .build();
    }

    public List<ConsumoClientesPorDiaResponseDTO> consumoClientesDiario(DashboardKpiFilter filter) {
        DashboardKpiFilter normalized = normalize(filter);
        DashboardPeriodo periodo = requirePeriodo(normalized);
        return DashboardKpiSeries.completarConsumo(periodo, consumoQuery.consumoPorDia(normalized, periodo));
    }

    public List<TopClientePgExpResponseDTO> topClientesPgExp(DashboardKpiFilter filter) {
        DashboardKpiFilter normalized = normalize(filter);
        DashboardPeriodo periodo = requirePeriodo(normalized);
        return consumoQuery.topClientesPgExp(normalized, periodo, properties);
    }

    public List<TopDepartamentoInsumosResponseDTO> topDepartamentosInsumos(DashboardKpiFilter filter) {
        DashboardKpiFilter normalized = normalize(filter);
        DashboardPeriodo periodo = requirePeriodo(normalized);
        return insumosQuery.topDepartamentos(periodo, normalized.getLimit());
    }

    public List<TopInsumoCompradoResponseDTO> topInsumosComprados(DashboardKpiFilter filter) {
        DashboardKpiFilter normalized = normalize(filter);
        DashboardPeriodo periodo = requirePeriodo(normalized);
        return insumosQuery.topInsumosComprados(periodo, normalized.getLimit());
    }

    public List<ClienteMasEquiposResponseDTO> clientesConMasEquipos(DashboardKpiFilter filter) {
        DashboardKpiFilter normalized = normalize(filter);
        DashboardPeriodo periodo = periodoOpcional(normalized);
        return equipoQuery.clientesConMasEquipos(normalized, periodo);
    }

    public List<ClienteSinConsumoResponseDTO> clientesSinConsumo(DashboardKpiFilter filter) {
        DashboardKpiFilter normalized = normalize(filter);
        return consumoQuery.clientesSinConsumo(normalized, normalized.getFechaCorte());
    }

    public DevolucionesEquiposResponseDTO devolucionesEquipos(DashboardKpiFilter filter) {
        DashboardKpiFilter normalized = normalize(filter);
        DashboardPeriodo periodo = requirePeriodo(normalized);
        return completarDevoluciones(equipoQuery.devoluciones(normalized, periodo), periodo);
    }

    public MovimientoEquiposDiarioResponseDTO movimientoEquiposDiario(DashboardKpiFilter filter) {
        DashboardKpiFilter normalized = normalize(filter);
        DashboardPeriodo periodo = requirePeriodo(normalized);
        return movimientoDiario(normalized, periodo);
    }

    public MetaVentaCumplidasResponseDTO metasEmpleadosCumplidas() {
        return metaVentaCoberturaService.listarCumplidasUltimaSemana();
    }

    private MovimientoEquiposDiarioResponseDTO movimientoDiario(DashboardKpiFilter filter, DashboardPeriodo periodo) {
        var serie = DashboardKpiSeries.completarMovimiento(periodo, equipoQuery.movimientoPorDia(filter, periodo));
        return MovimientoEquiposDiarioResponseDTO.builder()
                .movimientoEquiposPorDia(serie)
                .diaMayorMovimiento(DashboardKpiSeries.diaMayor(serie))
                .diaMenorMovimiento(DashboardKpiSeries.diaMenorConActividad(serie))
                .build();
    }

    private DevolucionesEquiposResponseDTO completarDevoluciones(DevolucionesEquiposResponseDTO devoluciones,
                                                                 DashboardPeriodo periodo) {
        if (devoluciones == null) {
            devoluciones = DevolucionesEquiposResponseDTO.vacio();
        }
        devoluciones.setPorDia(DashboardKpiSeries.completarCantidadPorDia(
                periodo,
                devoluciones.getPorDia() == null ? List.of() : devoluciones.getPorDia()));
        if (devoluciones.getPorTipoEquipo() == null) {
            devoluciones.setPorTipoEquipo(List.of());
        }
        return devoluciones;
    }

    private PeriodoKpiResponseDTO periodoResponse(DashboardPeriodo periodo) {
        return PeriodoKpiResponseDTO.builder()
                .fechaInicio(periodo.fechaInicio())
                .fechaFin(periodo.fechaFin())
                .zonaHoraria(DashboardTimeZone.ZONA_MEXICO.getId())
                .build();
    }

    private DashboardPeriodo requirePeriodo(DashboardKpiFilter filter) {
        try {
            return DashboardPeriodo.of(filter.getFechaInicio(), filter.getFechaFin());
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    private DashboardPeriodo periodoOpcional(DashboardKpiFilter filter) {
        if (filter.getFechaInicio() == null && filter.getFechaFin() == null) {
            LocalDate hoy = DashboardPeriodo.hoyMexico();
            return DashboardPeriodo.of(hoy, hoy);
        }
        if (filter.getFechaInicio() == null || filter.getFechaFin() == null) {
            LocalDate unica = filter.getFechaInicio() != null ? filter.getFechaInicio() : filter.getFechaFin();
            return DashboardPeriodo.of(unica, unica);
        }
        return requirePeriodo(filter);
    }

    private DashboardKpiFilter normalize(DashboardKpiFilter filter) {
        LocalDate fechaInicio = filter.getFechaInicio();
        LocalDate fechaFin = filter.getFechaFin();
        LocalDate fechaCorte = filter.getFechaCorte();
        if (fechaCorte == null) {
            fechaCorte = fechaFin != null ? fechaFin : DashboardPeriodo.hoyMexico();
        }
        boolean incluirNunca = filter.isIncluirNuncaConsumieron();
        return DashboardKpiFilter.builder()
                .fechaInicio(fechaInicio)
                .fechaFin(fechaFin)
                .fechaCorte(fechaCorte)
                .limit(normalizeLimit(filter.getLimit()))
                .incluirCancelados(filter.isIncluirCancelados())
                .incluirClientesInactivos(filter.isIncluirClientesInactivos())
                .incluirNuncaConsumieron(incluirNunca)
                .idRutas(filter.getIdRutas())
                .idClientes(filter.getIdClientes())
                .idTiposEquipo(filter.getIdTiposEquipo())
                .idEquipos(filter.getIdEquipos())
                .conceptosVenta(filter.getConceptosVenta())
                .build();
    }

    static int normalizeLimit(Integer limit) {
        if (limit == null || limit < 1) {
            return DEFAULT_LIMIT;
        }
        return Math.min(limit, MAX_LIMIT);
    }
}
