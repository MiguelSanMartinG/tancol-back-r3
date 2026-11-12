package com.main.tancol.dashboard.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardKpisOfficialResponseDTO {
    private PeriodoKpiResponseDTO periodo;
    @Builder.Default
    private List<ConsumoClientesPorDiaResponseDTO> consumoClientesPorDia = new ArrayList<>();
    @Builder.Default
    private List<TopClientePgExpResponseDTO> topClientesPgExp = new ArrayList<>();
    @Builder.Default
    private List<TopDepartamentoInsumosResponseDTO> topDepartamentosInsumos = new ArrayList<>();
    @Builder.Default
    private List<TopInsumoCompradoResponseDTO> topInsumosComprados = new ArrayList<>();
    @Builder.Default
    private List<ClienteMasEquiposResponseDTO> clientesConMasEquipos = new ArrayList<>();
    @Builder.Default
    private List<ClienteSinConsumoResponseDTO> clientesSinConsumo = new ArrayList<>();
    @Builder.Default
    private DevolucionesEquiposResponseDTO devolucionesEquipos = DevolucionesEquiposResponseDTO.vacio();
    @Builder.Default
    private List<MovimientoEquiposDiaResponseDTO> movimientoEquiposPorDia = new ArrayList<>();
    private DiaMovimientoExtremoResponseDTO diaMayorMovimiento;
    private DiaMovimientoExtremoResponseDTO diaMenorMovimiento;
    private DashboardKpiMetadataResponseDTO metadata;
}
