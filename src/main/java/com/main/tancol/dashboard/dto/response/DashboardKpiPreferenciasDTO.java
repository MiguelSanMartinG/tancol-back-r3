package com.main.tancol.dashboard.dto.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({"inlineKpis", "kpis", "charts", "official", "views", "colors"})
public class DashboardKpiPreferenciasDTO {
    private boolean inlineKpis;
    private Map<String, Boolean> kpis;
    private Map<String, Boolean> charts;
    private Map<String, Boolean> official;
    private Map<String, String> views;
    private Map<String, String> colors;
}
