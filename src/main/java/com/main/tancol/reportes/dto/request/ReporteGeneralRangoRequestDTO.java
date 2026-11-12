package com.main.tancol.reportes.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReporteGeneralRangoRequestDTO {
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
}
