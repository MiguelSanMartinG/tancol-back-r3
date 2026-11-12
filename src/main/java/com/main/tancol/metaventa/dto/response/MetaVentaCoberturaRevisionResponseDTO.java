package com.main.tancol.metaventa.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Getter
@Builder
public class MetaVentaCoberturaRevisionResponseDTO {
    private LocalDateTime periodoInicio;
    private LocalDateTime periodoFin;
    private LocalDateTime ejecutadoEn;
    private int totalEvaluadas;
    private int totalNoCubiertas;
    private int destinatariosEncontrados;
    private int notificacionesGeneradas;
    private List<MetaVentaCoberturaItemResponseDTO> noCubiertas;
}
