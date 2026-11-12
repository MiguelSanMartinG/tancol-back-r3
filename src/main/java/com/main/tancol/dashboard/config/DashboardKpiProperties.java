package com.main.tancol.dashboard.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ConfigurationProperties(prefix = "tancol.kpi.conceptos")
public class DashboardKpiProperties {

    private List<UUID> pg = new ArrayList<>(List.of(
            UUID.fromString("b6ccdf91-eb42-422a-b30c-bd0a028738fc")
    ));

    private List<UUID> exp = new ArrayList<>(List.of(
            UUID.fromString("3e397f5b-26f4-46b6-ab13-0f61b15108d0")
    ));

    public List<UUID> conceptosOficiales() {
        List<UUID> ids = new ArrayList<>();
        if (pg != null) {
            ids.addAll(pg);
        }
        if (exp != null) {
            ids.addAll(exp);
        }
        return ids;
    }

    public List<UUID> forzarConceptosOficiales(List<UUID> solicitados) {
        return conceptosOficiales();
    }

    public boolean esPg(UUID conceptoId) {
        return conceptoId != null && pg != null && pg.contains(conceptoId);
    }

    public boolean esExp(UUID conceptoId) {
        return conceptoId != null && exp != null && exp.contains(conceptoId);
    }
}
