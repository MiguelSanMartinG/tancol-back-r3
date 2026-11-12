package com.main.tancol.dashboard.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardKpiMetadataResponseDTO {
    private String generadoEn;
    private boolean incluyeCancelados;
}
