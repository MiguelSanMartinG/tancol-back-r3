package com.main.tancol.metaventa.service;

import com.main.tancol.empleados.entities.EmpleadosEntity;
import com.main.tancol.liquidacion.repositories.EmpleadoTotalizadoLiquidacionRepository;
import com.main.tancol.metaventa.dto.MetaVentaCoberturaResultado;
import com.main.tancol.metaventa.dto.response.MetaVentaCoberturaItemResponseDTO;
import com.main.tancol.metaventa.dto.response.MetaVentaCoberturaRevisionResponseDTO;
import com.main.tancol.metaventa.dto.response.MetaVentaCumplidasResponseDTO;
import com.main.tancol.metaventa.entities.MetaVentaEmpleadoEntity;
import com.main.tancol.metaventa.enums.PeriodoMetaVenta;
import com.main.tancol.metaventa.repositories.MetaVentaEmpleadoRepository;
import com.main.tancol.metaventa.util.MetaVentaValueMapper;
import com.main.tancol.notificacion.service.NotificacionService;
import com.main.tancol.producto.entities.comerciales.ProductoComercialEntity;
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
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
public class MetaVentaCoberturaService {

    public static final ZoneId ZONA_MEXICO = ZoneId.of("America/Mexico_City");
    private static final LocalTime HORA_CORTE = LocalTime.of(13, 0);

    private final MetaVentaEmpleadoRepository metaRepository;
    private final EmpleadoTotalizadoLiquidacionRepository totalizadoRepository;
    private final NotificacionService notificacionService;

    public MetaVentaCoberturaService(MetaVentaEmpleadoRepository metaRepository,
                                     EmpleadoTotalizadoLiquidacionRepository totalizadoRepository,
                                     NotificacionService notificacionService) {
        this.metaRepository = metaRepository;
        this.totalizadoRepository = totalizadoRepository;
        this.notificacionService = notificacionService;
    }

    /**
     * Semana en curso: lunes 00:00 inclusive → min(viernes 13:00, referencia) exclusive.
     * Así una revisión manual a mitad de semana usa el avance hasta el momento.
     */
    public LocalDateTime[] rangoSemanalActual(LocalDateTime referencia) {
        LocalDate fecha = referencia.toLocalDate();
        LocalDate lunes = fecha.with(DayOfWeek.MONDAY);
        LocalDateTime inicio = LocalDateTime.of(lunes, LocalTime.MIDNIGHT);
        LocalDate viernes = lunes.plusDays(4);
        LocalDateTime finCheckpoint = LocalDateTime.of(viernes, HORA_CORTE);
        LocalDateTime fin = referencia.isBefore(finCheckpoint) ? referencia : finCheckpoint;
        return new LocalDateTime[]{inicio, fin};
    }

    /**
     * Última semana ya cerrada: lunes 00:00 inclusive → viernes 13:00 exclusive.
     * Si el corte de la semana en curso todavía no ocurre, es la semana anterior.
     */
    public LocalDateTime[] rangoUltimaSemanaCerrada(LocalDateTime referencia) {
        LocalDate lunesActual = referencia.toLocalDate().with(DayOfWeek.MONDAY);
        LocalDateTime corteActual = LocalDateTime.of(lunesActual.plusDays(4), HORA_CORTE);
        LocalDate lunes = referencia.isBefore(corteActual) ? lunesActual.minusWeeks(1) : lunesActual;
        LocalDateTime inicio = LocalDateTime.of(lunes, LocalTime.MIDNIGHT);
        LocalDateTime fin = LocalDateTime.of(lunes.plusDays(4), HORA_CORTE);
        return new LocalDateTime[]{inicio, fin};
    }

    public LocalDateTime[] resolverRango(PeriodoMetaVenta periodo, LocalDateTime referencia) {
        if (periodo == null || periodo == PeriodoMetaVenta.SEMANAL) {
            return rangoSemanalActual(referencia);
        }
        throw new UnsupportedOperationException("Periodo de meta no soportado aún: " + periodo);
    }

    public MetaVentaCoberturaResultado evaluar(MetaVentaEmpleadoEntity meta, LocalDateTime referencia) {
        LocalDateTime[] rango = resolverRango(meta.getPeriodo(), referencia);
        return evaluarEnRango(meta, rango[0], rango[1]);
    }

    public MetaVentaCoberturaResultado evaluarEnRango(MetaVentaEmpleadoEntity meta,
                                                      LocalDateTime inicio,
                                                      LocalDateTime fin) {
        BigDecimal vendido = totalizadoRepository.sumCantidadByEmpleadoAndProductoAndFechaBetween(
                meta.getEmpleado().getId(),
                meta.getProducto().getId(),
                inicio,
                fin);
        if (vendido == null) {
            vendido = BigDecimal.ZERO;
        }
        BigDecimal minimo = meta.getCantidadMinima() != null ? meta.getCantidadMinima() : BigDecimal.ZERO;
        boolean ok = vendido.compareTo(minimo) >= 0;
        return MetaVentaCoberturaResultado.builder()
                .meta(meta)
                .periodoInicio(inicio)
                .periodoFin(fin)
                .cantidadMinima(minimo)
                .cantidadVendida(vendido)
                .cubierto(ok)
                .build();
    }

    @Transactional(readOnly = true)
    public List<MetaVentaCoberturaResultado> evaluarMetasSemanales(LocalDateTime referencia) {
        List<MetaVentaEmpleadoEntity> metas =
                metaRepository.findActivasByPeriodoConCantidad(PeriodoMetaVenta.SEMANAL);
        List<MetaVentaCoberturaResultado> resultados = new ArrayList<>();
        for (MetaVentaEmpleadoEntity meta : metas) {
            resultados.add(evaluar(meta, referencia));
        }
        return resultados;
    }

    /**
     * Empleados que cubrieron su meta en la última semana cerrada.
     * Una fila por empleado y producto. No genera notificaciones.
     */
    @Transactional(readOnly = true)
    public MetaVentaCumplidasResponseDTO listarCumplidasUltimaSemana() {
        LocalDateTime referencia = ZonedDateTime.now(ZONA_MEXICO).toLocalDateTime();
        LocalDateTime[] rango = rangoUltimaSemanaCerrada(referencia);
        List<MetaVentaEmpleadoEntity> metas =
                metaRepository.findVigentesEnPeriodo(PeriodoMetaVenta.SEMANAL, rango[0], rango[1]);

        List<MetaVentaCoberturaResultado> cumplidas = new ArrayList<>();
        for (MetaVentaEmpleadoEntity meta : metas) {
            MetaVentaCoberturaResultado resultado = evaluarEnRango(meta, rango[0], rango[1]);
            if (resultado.isCubierto()) {
                cumplidas.add(resultado);
            }
        }
        cumplidas.sort(Comparator
                .comparing((MetaVentaCoberturaResultado r) -> etiquetaOrden(r.getMeta().getProducto()),
                        String.CASE_INSENSITIVE_ORDER)
                .thenComparing(r -> {
                    String nombre = MetaVentaValueMapper.nombreEmpleado(r.getMeta().getEmpleado());
                    return nombre != null ? nombre : "";
                }, String.CASE_INSENSITIVE_ORDER));

        return MetaVentaCumplidasResponseDTO.builder()
                .periodoInicio(rango[0])
                .periodoFin(rango[1])
                .total(cumplidas.size())
                .cumplidas(cumplidas.stream().map(this::toItemResponse).toList())
                .build();
    }

    private String etiquetaOrden(ProductoComercialEntity producto) {
        if (producto == null || producto.getNombre() == null) {
            return "";
        }
        return producto.getNombre();
    }

    /**
     * Revisión de metas semanales (manual o cron). Genera notificaciones a usuarios con lectura
     * en el módulo de metas de ventas, aunque ya existan iguales.
     */
    @Transactional
    public MetaVentaCoberturaRevisionResponseDTO revisarMetasSemanales() {
        LocalDateTime referencia = ZonedDateTime.now(ZONA_MEXICO).toLocalDateTime();
        log.info("Iniciando revisión de metas semanales de venta a {}", referencia);

        List<MetaVentaCoberturaResultado> resultados = evaluarMetasSemanales(referencia);
        List<MetaVentaCoberturaResultado> incumplidas = resultados.stream()
                .filter(r -> !r.isCubierto())
                .toList();

        LocalDateTime periodoInicio;
        LocalDateTime periodoFin;
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
            List<EmpleadosEntity> destinatariosList = notificacionService.destinatariosMetasVentas();
            destinatarios = destinatariosList.size();
            if (destinatariosList.isEmpty()) {
                log.warn("Hay {} metas sin cubrir pero no hay destinatarios con lectura en módulo '{}'",
                        incumplidas.size(), NotificacionService.MODULO_METAS_VENTAS);
            } else {
                log.info("Destinatarios de notificación de metas de venta: {}", destinatarios);
                for (MetaVentaCoberturaResultado resultado : incumplidas) {
                    notificacionesGeneradas += notificarIncumplimiento(resultado, destinatariosList);
                }
            }
        }

        log.info("Revisión de metas finalizada. Evaluadas={}, no cubiertas={}, destinatarios={}, notificaciones={}",
                resultados.size(), incumplidas.size(), destinatarios, notificacionesGeneradas);

        return MetaVentaCoberturaRevisionResponseDTO.builder()
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

    private int notificarIncumplimiento(MetaVentaCoberturaResultado resultado, List<EmpleadosEntity> destinatarios) {
        MetaVentaEmpleadoEntity meta = resultado.getMeta();
        String empleadoNombre = MetaVentaValueMapper.nombreEmpleado(meta.getEmpleado());
        if (empleadoNombre == null) {
            empleadoNombre = "N/D";
        }
        String productoNombre = MetaVentaValueMapper.etiquetaProducto(meta.getProducto());

        String titulo = "Meta semanal de venta no cubierta";
        String mensaje = String.format(
                "El empleado %s no cubrió la meta semanal de %s. Mínimo: %s, vendido: %s. Periodo: %s a %s.",
                empleadoNombre,
                productoNombre,
                resultado.getCantidadMinima(),
                resultado.getCantidadVendida(),
                resultado.getPeriodoInicio(),
                resultado.getPeriodoFin()
        );

        return notificacionService.crearMetaVenta(
                destinatarios,
                titulo,
                mensaje,
                meta.getId(),
                resultado.getPeriodoInicio(),
                resultado.getPeriodoFin()
        );
    }

    private MetaVentaCoberturaItemResponseDTO toItemResponse(MetaVentaCoberturaResultado resultado) {
        MetaVentaEmpleadoEntity meta = resultado.getMeta();
        EmpleadosEntity empleado = meta.getEmpleado();
        ProductoComercialEntity producto = meta.getProducto();
        return MetaVentaCoberturaItemResponseDTO.builder()
                .metaId(meta.getId())
                .empleadoId(empleado != null ? empleado.getId() : null)
                .empleadoNombre(MetaVentaValueMapper.nombreEmpleado(empleado))
                .productoId(producto != null ? producto.getId() : null)
                .productoNombre(producto != null ? producto.getNombre() : null)
                .productoAlias(producto != null ? producto.getAlias() : null)
                .cantidadMinima(resultado.getCantidadMinima())
                .cantidadVendida(resultado.getCantidadVendida())
                .cubierto(resultado.isCubierto())
                .periodoInicio(resultado.getPeriodoInicio())
                .periodoFin(resultado.getPeriodoFin())
                .build();
    }
}
