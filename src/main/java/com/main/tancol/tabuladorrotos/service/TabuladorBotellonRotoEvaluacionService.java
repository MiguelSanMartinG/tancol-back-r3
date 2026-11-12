package com.main.tancol.tabuladorrotos.service;

import com.main.tancol.empleados.entities.EmpleadosEntity;
import com.main.tancol.logistica.entities.bitacora.MermaVentaTrayectoEntity;
import com.main.tancol.logistica.entities.bitacora.TrayectoEntity;
import com.main.tancol.logistica.entities.bitacora.VentaTrayectoEntity;
import com.main.tancol.logistica.repositories.MermaVentaTrayectoRepository;
import com.main.tancol.notificacion.service.NotificacionService;
import com.main.tancol.producto.entities.comerciales.ProductoComercialEntity;
import com.main.tancol.tabuladorrotos.entities.TabuladorBotellonRotoEntity;
import com.main.tancol.tabuladorrotos.repositories.TabuladorBotellonRotoRepository;
import com.main.tancol.tabuladorrotos.util.TabuladorBotellonRotoValueMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
public class TabuladorBotellonRotoEvaluacionService {

    static final ZoneId ZONA_MEXICO = ZoneId.of("America/Mexico_City");
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ISO_LOCAL_DATE;

    private final TabuladorBotellonRotoRepository tabuladorRepository;
    private final MermaVentaTrayectoRepository mermaRepository;
    private final NotificacionService notificacionService;

    public TabuladorBotellonRotoEvaluacionService(TabuladorBotellonRotoRepository tabuladorRepository,
                                                  MermaVentaTrayectoRepository mermaRepository,
                                                  NotificacionService notificacionService) {
        this.tabuladorRepository = tabuladorRepository;
        this.mermaRepository = mermaRepository;
        this.notificacionService = notificacionService;
    }

    @Transactional
    public void evaluar(TrayectoEntity viaje) {
        if (viaje == null || viaje.getVentaTrayecto() == null || viaje.getVentaTrayecto().isEmpty()) {
            return;
        }
        Set<Par> pares = new LinkedHashSet<>();
        for (VentaTrayectoEntity venta : viaje.getVentaTrayecto()) {
            if (venta.getProducto() == null || venta.getProducto().getId() == null || venta.getMermas() == null) {
                continue;
            }
            for (MermaVentaTrayectoEntity merma : venta.getMermas()) {
                if (merma.isRotoPorMaquina() || merma.getEmpleadoResponsable() == null
                        || merma.getEmpleadoResponsable().getId() == null) {
                    continue;
                }
                pares.add(new Par(merma.getEmpleadoResponsable().getId(), venta.getProducto().getId()));
            }
        }
        for (Par par : pares) {
            evaluarPar(par.empleadoId(), par.productoId());
        }
    }

    private void evaluarPar(UUID empleadoId, UUID productoId) {
        TabuladorBotellonRotoEntity tabulador = tabuladorRepository
                .findByEmpleado_IdAndProducto_IdAndDeletedFalseAndEnabledTrue(empleadoId, productoId)
                .orElse(null);
        if (tabulador == null) {
            return;
        }

        LocalDateTime[] semana = semanaActual();
        BigDecimal rotos = mermaRepository.sumRotosEmpleadoProductoEntre(empleadoId, productoId, semana[0], semana[1]);
        if (rotos == null) {
            rotos = BigDecimal.ZERO;
        }
        if (rotos.compareTo(tabulador.getTolerancia()) <= 0) {
            return;
        }

        var destinatarios = notificacionService.destinatariosBitacoraVenta();
        if (destinatarios.isEmpty()) {
            log.warn("El empleado {} superó la tolerancia de botellones rotos pero no hay destinatarios con lectura en '{}'",
                    empleadoId, NotificacionService.MODULO_BITACORA_VENTA);
            return;
        }

        String nombreEmpleado = nombre(tabulador.getEmpleado());
        String producto = nombreProducto(tabulador.getProducto());
        String unidad = tabulador.getUnidadMedida() != null && tabulador.getUnidadMedida().getNombre() != null
                ? tabulador.getUnidadMedida().getNombre()
                : "";
        String titulo = "Tolerancia de botellones rotos superada";
        String mensaje = String.format(
                "El empleado %s superó la tolerancia de botellones rotos de %s. Rotos en la semana: %s %s. Tolerancia: %s %s. Semana del %s al %s.",
                nombreEmpleado,
                producto,
                rotos.stripTrailingZeros().toPlainString(),
                unidad,
                tabulador.getTolerancia().stripTrailingZeros().toPlainString(),
                unidad,
                semana[0].toLocalDate().format(FECHA),
                semana[1].toLocalDate().format(FECHA)
        ).replaceAll(" +", " ").trim();

        int creadas = notificacionService.crearBotellonRoto(
                destinatarios, titulo, mensaje, empleadoId, productoId, semana[0], semana[1]);
        log.info("Tolerancia de botellones rotos superada. empleado={}, producto={}, rotos={}, notificaciones={}",
                empleadoId, productoId, rotos, creadas);
    }

    static LocalDateTime[] semanaActual() {
        LocalDate lunes = ZonedDateTime.now(ZONA_MEXICO).toLocalDate().with(DayOfWeek.MONDAY);
        LocalDateTime inicio = lunes.atStartOfDay();
        LocalDateTime fin = lunes.plusDays(6).atTime(LocalTime.of(23, 59, 59));
        return new LocalDateTime[]{inicio, fin};
    }

    private static String nombre(EmpleadosEntity empleado) {
        String nombre = TabuladorBotellonRotoValueMapper.nombreEmpleado(empleado);
        return nombre == null ? "Sin nombre" : nombre;
    }

    private static String nombreProducto(ProductoComercialEntity producto) {
        if (producto == null || producto.getNombre() == null || producto.getNombre().isBlank()) {
            return "producto";
        }
        return producto.getNombre().trim();
    }

    private record Par(UUID empleadoId, UUID productoId) {
    }
}
