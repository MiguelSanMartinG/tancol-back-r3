package com.main.tancol.prestamo.service;

import com.main.tancol.empleados.entities.EmpleadosEntity;
import com.main.tancol.liquidacion.repositories.LiquidacionPartidaRepository;
import com.main.tancol.notificacion.enums.TipoNotificacion;
import com.main.tancol.notificacion.service.NotificacionService;
import com.main.tancol.prestamo.dto.EquipoCoberturaResultado;
import com.main.tancol.prestamo.dto.response.EquipoCoberturaItemResponseDTO;
import com.main.tancol.prestamo.dto.response.EquipoCoberturaRevisionResponseDTO;
import com.main.tancol.prestamo.entities.EquipoAsignacionEntity;
import com.main.tancol.prestamo.enums.PeriodoCoberturaEquipo;
import com.main.tancol.prestamo.repositories.EquipoAsignacionRepository;
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
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class EquipoCoberturaService {

    public static final ZoneId ZONA_MEXICO = ZoneId.of("America/Mexico_City");

    private final EquipoAsignacionRepository asignacionRepository;
    private final LiquidacionPartidaRepository partidaRepository;
    private final NotificacionService notificacionService;

    public EquipoCoberturaService(EquipoAsignacionRepository asignacionRepository,
                                  LiquidacionPartidaRepository partidaRepository,
                                  NotificacionService notificacionService) {
        this.asignacionRepository = asignacionRepository;
        this.partidaRepository = partidaRepository;
        this.notificacionService = notificacionService;
    }

    /**
     * Semana en curso: lunes 00:00 inclusive → min(domingo 12:00, referencia) exclusive.
     * Así una revisión manual a mitad de semana usa el avance hasta el momento.
     */
    public LocalDateTime[] rangoSemanalActual(LocalDateTime referencia) {
        LocalDate fecha = referencia.toLocalDate();
        LocalDate lunes = fecha.with(DayOfWeek.MONDAY);
        LocalDateTime inicio = LocalDateTime.of(lunes, LocalTime.MIDNIGHT);
        LocalDate domingo = lunes.plusDays(6);
        LocalDateTime finCheckpoint = LocalDateTime.of(domingo, LocalTime.NOON);
        LocalDateTime fin = referencia.isBefore(finCheckpoint) ? referencia : finCheckpoint;
        return new LocalDateTime[]{inicio, fin};
    }

    public LocalDateTime[] resolverRango(PeriodoCoberturaEquipo periodo, LocalDateTime referencia) {
        if (periodo == null || periodo == PeriodoCoberturaEquipo.SEMANAL) {
            return rangoSemanalActual(referencia);
        }
        throw new UnsupportedOperationException("Periodo de cobertura no soportado aún: " + periodo);
    }

    public EquipoCoberturaResultado evaluar(EquipoAsignacionEntity asignacion, LocalDateTime referencia) {
        LocalDateTime[] rango = resolverRango(asignacion.getPeriodoCobertura(), referencia);
        BigDecimal cubierto = partidaRepository.sumImporteByClienteAndFechaBetween(
                asignacion.getCliente().getId(), rango[0], rango[1]);
        if (cubierto == null) {
            cubierto = BigDecimal.ZERO;
        }
        BigDecimal minimo = asignacion.getMontoMinimo() != null ? asignacion.getMontoMinimo() : BigDecimal.ZERO;
        boolean ok = cubierto.compareTo(minimo) >= 0;
        return EquipoCoberturaResultado.builder()
                .asignacion(asignacion)
                .periodoInicio(rango[0])
                .periodoFin(rango[1])
                .montoMinimo(minimo)
                .montoCubierto(cubierto)
                .cubierto(ok)
                .build();
    }

    @Transactional(readOnly = true)
    public List<EquipoCoberturaResultado> evaluarAsignacionesSemanales(LocalDateTime referencia) {
        List<EquipoAsignacionEntity> asignaciones =
                asignacionRepository.findActivasByPeriodoConMonto(PeriodoCoberturaEquipo.SEMANAL);
        List<EquipoCoberturaResultado> resultados = new ArrayList<>();
        for (EquipoAsignacionEntity asignacion : asignaciones) {
            resultados.add(evaluar(asignacion, referencia));
        }
        return resultados;
    }

    @Transactional(readOnly = true)
    public List<EquipoCoberturaResultado> evaluarAsignacionesSemanalesNoCubiertas(LocalDateTime referencia) {
        return evaluarAsignacionesSemanales(referencia).stream()
                .filter(r -> !r.isCubierto())
                .toList();
    }

    /**
     * Revisión de cobertura semanal (manual o cron). Genera notificaciones a usuarios con lectura
     * en el módulo de asignación de equipos, aunque ya existan iguales.
     */
    @Transactional
    public EquipoCoberturaRevisionResponseDTO revisarCoberturaSemanal() {
        LocalDateTime referencia = ZonedDateTime.now(ZONA_MEXICO).toLocalDateTime();
        log.info("Iniciando revisión de cobertura semanal de equipos a {}", referencia);

        List<EquipoCoberturaResultado> resultados = evaluarAsignacionesSemanales(referencia);
        List<EquipoCoberturaResultado> incumplidas = resultados.stream()
                .filter(r -> !r.isCubierto())
                .toList();

        LocalDateTime periodoInicio = null;
        LocalDateTime periodoFin = null;
        if (!resultados.isEmpty()) {
            periodoInicio = resultados.get(0).getPeriodoInicio();
            periodoFin = resultados.get(0).getPeriodoFin();
        } else {
            LocalDateTime[] rango = rangoSemanalActual(referencia);
            periodoInicio = rango[0];
            periodoFin = rango[1];
        }

        int notificacionesGeneradas = 0;
        int destinatarios = 0;
        if (!incumplidas.isEmpty()) {
            List<EmpleadosEntity> destinatariosList = notificacionService.destinatariosEquiposPrestamo();
            destinatarios = destinatariosList.size();
            if (destinatariosList.isEmpty()) {
                log.warn("Hay {} asignaciones sin cobertura pero no hay destinatarios con lectura en módulo '{}'",
                        incumplidas.size(), NotificacionService.MODULO_ASIGNACION_EQUIPOS);
            } else {
                log.info("Destinatarios de notificación de cobertura: {}", destinatarios);
                for (EquipoCoberturaResultado resultado : incumplidas) {
                    notificacionesGeneradas += notificarIncumplimiento(resultado, destinatariosList);
                }
            }
        }

        log.info("Revisión de cobertura finalizada. Evaluadas={}, no cubiertas={}, destinatarios={}, notificaciones={}",
                resultados.size(), incumplidas.size(), destinatarios, notificacionesGeneradas);

        return EquipoCoberturaRevisionResponseDTO.builder()
                .periodoInicio(periodoInicio)
                .periodoFin(periodoFin)
                .ejecutadoEn(referencia)
                .totalEvaluadas(resultados.size())
                .totalNoCubiertas(incumplidas.size())
                .destinatariosEncontrados(destinatarios)
                .notificacionesGeneradas(notificacionesGeneradas)
                .noCubiertas(incumplidas.stream().map(this::toItemResponse).toList())
                .build();
    }

    private int notificarIncumplimiento(EquipoCoberturaResultado resultado, List<EmpleadosEntity> destinatarios) {
        EquipoAsignacionEntity asignacion = resultado.getAsignacion();
        String clienteNombre = asignacion.getCliente() != null
                ? asignacion.getCliente().getNombreComercial()
                : "N/D";
        String equipoNombre = asignacion.getEquipo() != null
                ? asignacion.getEquipo().getNombre()
                : "N/D";

        String titulo = "Monto semanal de equipo no cubierto";
        String mensaje = String.format(
                "El cliente %s no cubrió el monto mínimo semanal del equipo %s. Mínimo: %s, cubierto: %s. Periodo: %s a %s.",
                clienteNombre,
                equipoNombre,
                resultado.getMontoMinimo(),
                resultado.getMontoCubierto(),
                resultado.getPeriodoInicio(),
                resultado.getPeriodoFin()
        );

        return notificacionService.crearParaUsuarios(
                destinatarios,
                titulo,
                mensaje,
                TipoNotificacion.EQUIPO_MONTO_NO_CUBIERTO,
                asignacion.getId(),
                asignacion.getCliente() != null ? asignacion.getCliente().getId() : null,
                resultado.getPeriodoInicio(),
                resultado.getPeriodoFin()
        );
    }

    private EquipoCoberturaItemResponseDTO toItemResponse(EquipoCoberturaResultado resultado) {
        EquipoAsignacionEntity asignacion = resultado.getAsignacion();
        return EquipoCoberturaItemResponseDTO.builder()
                .asignacionId(asignacion.getId())
                .clienteId(asignacion.getCliente() != null ? asignacion.getCliente().getId() : null)
                .clienteNombre(asignacion.getCliente() != null ? asignacion.getCliente().getNombreComercial() : null)
                .equipoId(asignacion.getEquipo() != null ? asignacion.getEquipo().getId() : null)
                .equipoNombre(asignacion.getEquipo() != null ? asignacion.getEquipo().getNombre() : null)
                .montoMinimo(resultado.getMontoMinimo())
                .montoCubierto(resultado.getMontoCubierto())
                .cubierto(resultado.isCubierto())
                .periodoInicio(resultado.getPeriodoInicio())
                .periodoFin(resultado.getPeriodoFin())
                .build();
    }
}
