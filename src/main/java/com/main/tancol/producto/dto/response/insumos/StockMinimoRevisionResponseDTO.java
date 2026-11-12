package com.main.tancol.producto.dto.response.insumos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Getter
@Builder
public class StockMinimoRevisionResponseDTO {
    private LocalDateTime ejecutadoEn;
    private int totalEvaluadas;
    private int totalBajoMinimo;
    private int destinatariosEncontrados;
    private int notificacionesGeneradas;
    private List<StockMinimoItemResponseDTO> bajoMinimo;
}
