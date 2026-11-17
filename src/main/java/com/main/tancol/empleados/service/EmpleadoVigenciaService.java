package com.main.tancol.empleados.service;

import com.main.tancol.empleados.entities.EmpleadosEntity;
import com.main.tancol.empleados.enums.TipoContratacion;
import com.main.tancol.empleados.repositories.EmpleadosRepository;
import com.main.tancol.notificacion.service.NotificacionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Avisa en la campana, con {@value #DIAS_ANTICIPACION} días de anticipación, la vigencia por vencer
 * de empleados temporales y practicantes. Los destinatarios son los roles con el tipo de notificación
 * VIGENCIA_EMPLEADO_POR_VENCER activo. Cada vigencia se avisa una sola vez.
 */
@Slf4j
@Service
public class EmpleadoVigenciaService {

    public static final ZoneId ZONA_MEXICO = ZoneId.of("America/Mexico_City");
    public static final int DIAS_ANTICIPACION = 2;
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final EmpleadosRepository empleadosRepository;
    private final NotificacionService notificacionService;

    public EmpleadoVigenciaService(EmpleadosRepository empleadosRepository, NotificacionService notificacionService) {
        this.empleadosRepository = empleadosRepository;
        this.notificacionService = notificacionService;
    }

    @Transactional
    public int revisarVigencias() {
        return revisarVigencias(LocalDate.now(ZONA_MEXICO));
    }

    /** Incluye las vigencias de hoy a hoy + 2 días, por si el aviso de un día no se pudo generar. */
    @Transactional
    public int revisarVigencias(LocalDate hoy) {
        List<EmpleadosEntity> porVencer = empleadosRepository.findConVigenciaEntre(
                EnumSet.of(TipoContratacion.TEMPORAL, TipoContratacion.PRACTICANTE),
                hoy, hoy.plusDays(DIAS_ANTICIPACION));
        if (porVencer.isEmpty()) {
            return 0;
        }
        List<EmpleadosEntity> destinatarios = notificacionService.destinatariosVigenciaEmpleados();
        int creadas = 0;
        for (EmpleadosEntity empleado : porVencer) {
            LocalDate vigencia = empleado.getFechaVigencia();
            String nombre = nombreCompleto(empleado);
            String titulo = "Vigencia por vencer: " + nombre;
            String mensaje = mensaje(nombre, empleado.getTipoContratacion(), vigencia, hoy);
            creadas += notificacionService.crearVigenciaEmpleado(destinatarios, empleado.getId(), titulo, mensaje,
                    vigencia.minusDays(DIAS_ANTICIPACION + 1L).atStartOfDay());
        }
        log.info("Revisión de vigencias de empleados: {} por vencer, {} notificaciones creadas", porVencer.size(), creadas);
        return creadas;
    }

    static String mensaje(String nombre, TipoContratacion tipo, LocalDate vigencia, LocalDate hoy) {
        long dias = java.time.temporal.ChronoUnit.DAYS.between(hoy, vigencia);
        String cuando = dias <= 0 ? "hoy" : dias == 1 ? "mañana" : "en " + dias + " días";
        return nombre + " (" + tipo.etiqueta() + ") termina su vigencia " + cuando + ", el " + vigencia.format(FORMATO) + ".";
    }

    private static String nombreCompleto(EmpleadosEntity e) {
        return Stream.of(e.getNombre(), e.getPrimerApellido(), e.getSegundoApellido())
                .filter(parte -> parte != null && !parte.isBlank())
                .map(String::trim)
                .collect(Collectors.joining(" "));
    }
}
