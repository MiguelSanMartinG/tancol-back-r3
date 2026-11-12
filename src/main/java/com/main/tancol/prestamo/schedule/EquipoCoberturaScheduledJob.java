package com.main.tancol.prestamo.schedule;

import com.main.tancol.prestamo.service.EquipoCoberturaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EquipoCoberturaScheduledJob {

    private final EquipoCoberturaService coberturaService;

    public EquipoCoberturaScheduledJob(EquipoCoberturaService coberturaService) {
        this.coberturaService = coberturaService;
    }

    @Scheduled(cron = "0 0 12 * * SUN", zone = "America/Mexico_City")
    public void evaluarCoberturaSemanal() {
        coberturaService.revisarCoberturaSemanal();
    }
}
