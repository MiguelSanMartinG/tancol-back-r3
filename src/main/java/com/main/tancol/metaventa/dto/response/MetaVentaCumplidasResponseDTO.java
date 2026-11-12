package com.main.tancol.metaventa.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Getter
@Builder
public class MetaVentaCumplidasResponseDTO {
    private LocalDateTime periodoInicio;
    private LocalDateTime periodoFin;
    private int total;
    private List<MetaVentaCoberturaItemResponseDTO> cumplidas;
}
