package com.main.tancol.empleados.schedule;

import com.main.tancol.empleados.service.EmpleadoVigenciaService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** Revisión diaria de vigencias de empleados temporales y practicantes (8:00, hora de México). */
@Component
public class EmpleadoVigenciaScheduledJob {

    private final EmpleadoVigenciaService vigenciaService;

    public EmpleadoVigenciaScheduledJob(EmpleadoVigenciaService vigenciaService) {
        this.vigenciaService = vigenciaService;
    }

    @Scheduled(cron = "0 0 8 * * *", zone = "America/Mexico_City")
    public void revisarVigencias() {
        vigenciaService.revisarVigencias();
    }
}
