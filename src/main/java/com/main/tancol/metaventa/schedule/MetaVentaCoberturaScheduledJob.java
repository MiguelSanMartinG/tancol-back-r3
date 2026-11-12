package com.main.tancol.metaventa.schedule;

import com.main.tancol.metaventa.service.MetaVentaCoberturaService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MetaVentaCoberturaScheduledJob {

    private final MetaVentaCoberturaService coberturaService;

    public MetaVentaCoberturaScheduledJob(MetaVentaCoberturaService coberturaService) {
        this.coberturaService = coberturaService;
    }

    @Scheduled(cron = "0 0 13 * * FRI", zone = "America/Mexico_City")
    public void evaluarMetasSemanales() {
        coberturaService.revisarMetasSemanales();
    }
}
