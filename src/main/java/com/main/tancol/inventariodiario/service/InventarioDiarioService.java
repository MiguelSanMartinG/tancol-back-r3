package com.main.tancol.inventariodiario.service;

import com.main.tancol.almacen.entities.AlmacenEntity;
import com.main.tancol.almacen.entities.InventarioEntity;
import com.main.tancol.almacen.repositories.AlmacenRepository;
import com.main.tancol.common.exceptions.exeption.GenericExeption;
import com.main.tancol.common.exeptions.IDNotFound;
import com.main.tancol.empleados.entities.DepartamentoEntity;
import com.main.tancol.empleados.entities.EmpleadosEntity;
import com.main.tancol.empleados.helpers.EmpleadoContext;
import com.main.tancol.empleados.repositories.DepartamentoRepository;
import com.main.tancol.empleados.repositories.EmpleadosRepository;
import com.main.tancol.inventariodiario.dto.InvDiarioDTOs.*;
import com.main.tancol.inventariodiario.entities.*;
import com.main.tancol.inventariodiario.enums.ConceptoEntrega;
import com.main.tancol.inventariodiario.enums.EstatusSolicitud;
import com.main.tancol.inventariodiario.repositories.*;
import com.main.tancol.logistica.entities.rutas.RutasEntity;
import com.main.tancol.logistica.repositories.RutasRepository;
import com.main.tancol.notificacion.enums.TipoNotificacion;
import com.main.tancol.notificacion.service.NotificacionService;
import com.main.tancol.permisos.service.PermisoAvanzadoService;
import com.main.tancol.producto.entities.insumos.ProductoBasicoEntity;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Inventario diario de insumos y solicitudes de stock (Buzón 1, 2 y 3).
 * <p>
 * El inventario físico vive solo en el almacén. Cada área (departamento) tiene un cupo diario por
 * insumo. Las partidas descuentan del cupo del día y de la existencia del almacén al firmarse, en
 * la misma transacción; lo registrado sin firma solo cuenta para no pasarse del cupo. Una solicitud
 * de stock recibida amplía el cupo del día (no mueve existencia: la existencia solo la mueven las
 * partidas, para no descontar dos veces). Sin confirmación: cada día inicia con la base de la
 * tarjeta (hora de México). La confirmación del día queda solo por compatibilidad.
 */
@Service
@RequiredArgsConstructor
public class InventarioDiarioService {

    static final ZoneId ZONA = ZoneId.of("America/Mexico_City");
    static final String MAQUINA = "MAQUINA";
    static final String PERMISO_AREA = "RESPONSABLE_AREA";
    static final String PERMISO_ALMACEN = "RESPONSABLE_ALMACEN";
    private static final Set<EstatusSolicitud> ABIERTAS = EnumSet.of(EstatusSolicitud.SOLICITADO, EstatusSolicitud.ACEPTADO);
    /** Segundos que tiene el solicitante para ajustar cantidad y nota después de enviar. */
    static final long SEGUNDOS_EDITAR_SOLICITUD = 120;
    /** Id que no existe: «sin partida que excluir» en las consultas. */
    private static final UUID SIN_ID = new UUID(0L, 0L);
    private static final Path FIRMAS = Paths.get("uploads");
    private static final Map<String, String> EXT_FIRMA = Map.of("image/png", ".png", "image/jpeg", ".jpg", "image/webp", ".webp");
    private static final Pattern NOMBRE_ARCHIVO = Pattern.compile("^[A-Za-z0-9._-]{1,300}$");

    /** Tarjetas por defecto de cada área: insumo (por nombre), base y mínimo (Buzón 5). */
    private static final List<Object[]> POR_DEFECTO = List.of(
            new Object[]{Pattern.compile("envase.*\\b20\\s*(l|lt|lts|litros)?\\b"), 25, 5},
            new Object[]{Pattern.compile("envase.*\\b11\\s*(l|lt|lts|litros)?\\b"), 25, 5},
            new Object[]{Pattern.compile("hielo.*\\b3\\s*kg"), 700, 5},
            new Object[]{Pattern.compile("hielo.*\\b5\\s*kg"), 700, 5});

    private final CupoRepository cupoRepository;
    private final CupoDiaRepository cupoDiaRepository;
    private final ConfirmacionRepository confirmacionRepository;
    private final PartidaRepository partidaRepository;
    private final SolicitudStockRepository solicitudRepository;
    private final InvDiarioInventarioRepository inventarioRepository;
    private final DepartamentoRepository departamentoRepository;
    private final AlmacenRepository almacenRepository;
    private final EmpleadosRepository empleadosRepository;
    private final RutasRepository rutasRepository;
    private final PermisoAvanzadoService permisoAvanzadoService;
    private final NotificacionService notificacionService;

    // ================================================================== catálogos y permisos

    @Transactional(readOnly = true)
    public Catalogos catalogos() {
        List<Area> areas = todos(departamentoRepository.findAll())
                .filter(d -> !d.isDeleted())
                .sorted(Comparator.comparing(d -> Optional.ofNullable(d.getNombre()).orElse("")))
                .map(this::toArea).toList();
        Map<UUID, Long> totales = new HashMap<>();
        for (Object[] fila : inventarioRepository.totalesPorAlmacen()) {
            totales.put((UUID) fila[0], ((Number) fila[1]).longValue());
        }
        List<Almacen> almacenes = todos(almacenRepository.findAll())
                .filter(a -> !a.isDeleted())
                .sorted(Comparator.comparing(a -> Optional.ofNullable(a.getNombre()).orElse("")))
                .map(a -> new Almacen(a.getId(), a.getNombre(),
                        a.getResponsable() != null ? a.getResponsable().getId() : null, nombre(a.getResponsable()),
                        totales.getOrDefault(a.getId(), 0L)))
                .toList();
        List<Persona> empleados = todos(empleadosRepository.findAll())
                .filter(e -> !e.isDeleted() && e.isEnabled())
                .map(e -> new Persona(e.getId(), nombre(e)))
                .sorted(Comparator.comparing(Persona::nombre)).toList();
        List<Ruta> rutas = todos(rutasRepository.findAll())
                .filter(r -> !r.isDeleted())
                .map(r -> new Ruta(r.getId(), r.getNombre()))
                .sorted(Comparator.comparing(r -> Optional.ofNullable(r.nombre()).orElse(""))).toList();
        return new Catalogos(areas, almacenes, empleados, rutas);
    }

    /** Insumos con registro en el inventario del almacén, con existencia y unidad. */
    @Transactional(readOnly = true)
    public List<Insumo> insumosDeAlmacen(UUID almacenId) {
        return inventarioRepository.delAlmacen(almacenId).stream()
                .map(i -> new Insumo(i.getProductoBasicoEntity().getId(), i.getProductoBasicoEntity().getNombre(),
                        unidad(i), cero(i.getCantidad())))
                .toList();
    }

    @Transactional(readOnly = true)
    public MisPermisos misPermisos() {
        EmpleadosEntity yo = empleadoActual();
        boolean admin = esAdmin(yo);
        List<UUID> areas;
        if (admin) {
            areas = todos(departamentoRepository.findAll()).filter(d -> !d.isDeleted()).map(DepartamentoEntity::getId).toList();
        } else if (permisoAvanzadoService.hasAdvancedPermission(yo, PERMISO_AREA) && yo.getDepartamento() != null) {
            areas = List.of(yo.getDepartamento().getId());
        } else {
            areas = List.of();
        }
        List<UUID> almacenes;
        if (admin || permisoAvanzadoService.hasAdvancedPermission(yo, PERMISO_ALMACEN)) {
            almacenes = todos(almacenRepository.findAll()).filter(a -> !a.isDeleted()).map(AlmacenEntity::getId).toList();
        } else {
            almacenes = todos(almacenRepository.findAll())
                    .filter(a -> !a.isDeleted() && a.getResponsable() != null && a.getResponsable().getId().equals(yo.getId()))
                    .map(AlmacenEntity::getId).toList();
        }
        return new MisPermisos(admin, areas, almacenes);
    }

    // ================================================================== cupos

    @Transactional
    public List<Cupo> cupos(UUID areaId, LocalDate fecha) {
        DepartamentoEntity area = area(areaId);
        sembrarPorDefecto(area);
        LocalDate dia = fecha != null ? fecha : hoy();
        return cupoRepository.findByArea_IdAndDeletedFalseOrderByPorDefectoDescCreatedAtAsc(areaId).stream()
                .map(c -> toCupo(c, dia))
                .toList();
    }

    @Transactional
    public Cupo crearCupo(CupoCrear dto) {
        DepartamentoEntity area = area(dto.areaId());
        exigirResponsableArea(area);
        AlmacenEntity almacen = almacen(dto.almacenId());
        exigirAlmacenPermitido(almacen);
        validarBaseMinimo(dto.base(), dto.minimo());
        InventarioEntity inv = inventarioRepository.existencia(almacen.getId(), dto.insumoId())
                .orElseThrow(() -> new GenericExeption("El insumo no está registrado en el inventario de ese almacén."));
        if (cupoRepository.findFirstByArea_IdAndInsumo_IdAndDeletedFalse(area.getId(), dto.insumoId()).isPresent()) {
            throw new GenericExeption("El área ya tiene una tarjeta de este insumo.");
        }
        CupoEntity cupo = cupoRepository.save(CupoEntity.builder()
                .area(area).almacen(almacen).insumo(inv.getProductoBasicoEntity())
                .porDefecto(false).base(dto.base()).minimo(dto.minimo()).creadoPor(empleadoActual()).build());
        return toCupo(cupo, hoy());
    }

    /** La nueva base aplica desde hoy. */
    @Transactional
    public Cupo editarCupo(UUID id, CupoEditar dto) {
        CupoEntity cupo = cupo(id);
        exigirResponsableArea(cupo.getArea());
        validarBaseMinimo(dto.base(), dto.minimo());
        cupo.setBase(dto.base());
        cupo.setMinimo(dto.minimo());
        cupoRepository.save(cupo);
        cupoDiaRepository.findByCupo_IdAndFecha(cupo.getId(), hoy()).ifPresent(dia -> {
            dia.setInicial(dto.base());
            dia.setFaltante(BigDecimal.ZERO);
            cupoDiaRepository.save(dia);
        });
        return toCupo(cupo, hoy());
    }

    /** Solo quien agregó la tarjeta la elimina; las tarjetas por defecto no se eliminan. */
    @Transactional
    public void eliminarCupo(UUID id) {
        CupoEntity cupo = cupo(id);
        if (cupo.isPorDefecto()) {
            throw new GenericExeption("Las tarjetas por defecto del área no se pueden eliminar.");
        }
        EmpleadosEntity yo = empleadoActual();
        if (cupo.getCreadoPor() == null || !cupo.getCreadoPor().getId().equals(yo.getId())) {
            throw new AccessDeniedException("Solo quien agregó la tarjeta la puede eliminar.");
        }
        cupo.setDeleted(true);
        cupoRepository.save(cupo);
    }

    // ================================================================== confirmación del día

    @Transactional(readOnly = true)
    public Confirmacion confirmacion(UUID areaId, LocalDate fecha) {
        return confirmacionRepository.findByArea_IdAndFechaAndDeletedFalse(areaId, fecha != null ? fecha : hoy())
                .map(this::toConfirmacion).orElse(null);
    }

    @Transactional
    public List<PreviaCupo> previa(UUID areaId) {
        DepartamentoEntity area = area(areaId);
        sembrarPorDefecto(area);
        return cupoRepository.findByArea_IdAndDeletedFalseOrderByPorDefectoDescCreatedAtAsc(areaId).stream()
                .map(c -> {
                    BigDecimal existencia = existencia(c.getAlmacen().getId(), c.getInsumo().getId());
                    BigDecimal entrega = c.getBase().min(existencia.max(BigDecimal.ZERO));
                    return new PreviaCupo(c.getId(), c.getInsumo().getId(), c.getInsumo().getNombre(), unidad(c.getInsumo()),
                            c.getBase(), existencia, entrega, c.getBase().subtract(entrega));
                }).toList();
    }

    /** Entrega por tarjeta min(base, existencia); si no alcanza, avisa al responsable del almacén. */
    @Transactional
    public ResultadoConfirmacion confirmar(ConfirmarDia dto) {
        DepartamentoEntity area = area(dto.areaId());
        exigirResponsableArea(area);
        LocalDate hoy = hoy();
        if (confirmado(area.getId(), hoy)) {
            throw new GenericExeption("El cupo de hoy ya fue confirmado.");
        }
        List<Faltante> faltantes = new ArrayList<>();
        for (PreviaCupo p : previa(area.getId())) {
            CupoEntity cupo = cupo(p.cupoId());
            CupoDiaEntity dia = cupoDiaRepository.findByCupo_IdAndFecha(cupo.getId(), hoy)
                    .orElseGet(() -> nuevoDia(cupo, hoy, BigDecimal.ZERO, BigDecimal.ZERO));
            dia.setInicial(p.seEntrega());
            dia.setFaltante(p.faltante());
            cupoDiaRepository.save(dia);
            if (p.faltante().signum() > 0) {
                faltantes.add(new Faltante(p.insumoId(), p.insumoNombre(), p.faltante()));
                notificar(destinatariosAlmacen(cupo.getAlmacen(), TipoNotificacion.DOTACION_INCOMPLETA),
                        "Dotación incompleta",
                        "Dotación incompleta en " + area.getNombre() + ": " + p.insumoNombre() + " recibió "
                                + num(p.seEntrega()) + " de " + num(p.base()) + " del " + cupo.getAlmacen().getNombre() + ".",
                        TipoNotificacion.DOTACION_INCOMPLETA);
            }
        }
        ConfirmacionEntity conf = confirmacionRepository.save(ConfirmacionEntity.builder()
                .area(area).fecha(hoy).confirmadoPor(empleadoActual()).confirmadoEn(ahora()).build());
        return new ResultadoConfirmacion(toConfirmacion(conf), faltantes);
    }

    // ================================================================== partidas

    @Transactional(readOnly = true)
    public List<Partida> partidas(UUID areaId, LocalDate inicio, LocalDate fin, UUID empleadoId) {
        LocalDate[] r = rango(inicio, fin);
        Specification<PartidaEntity> spec = (root, q, cb) -> {
            List<Predicate> ps = new ArrayList<>();
            ps.add(cb.isFalse(root.get("deleted")));
            ps.add(cb.greaterThanOrEqualTo(root.get("fecha"), r[0].atStartOfDay()));
            ps.add(cb.lessThan(root.get("fecha"), r[1].plusDays(1).atStartOfDay()));
            if (areaId != null) ps.add(cb.equal(root.get("area").get("id"), areaId));
            if (empleadoId != null) ps.add(cb.or(cb.equal(root.get("usuario").get("id"), empleadoId),
                    cb.equal(root.get("firmante").get("id"), empleadoId)));
            return cb.and(ps.toArray(Predicate[]::new));
        };
        return partidaRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "fecha")).stream().map(this::toPartida).toList();
    }

    /** Se registra sin descontar: descuenta al firmarse. Lo pendiente de firma sí cuenta para no pasarse del cupo. */
    @Transactional
    public Partida crearPartida(PartidaCrear dto) {
        DepartamentoEntity area = area(dto.areaId());
        validarCantidad(dto.cantidad());
        if (dto.concepto() == null) throw new GenericExeption("Elige el concepto de entrega.");
        CupoEntity cupo = cupoRepository.findFirstByArea_IdAndInsumo_IdAndDeletedFalse(area.getId(), dto.insumoId())
                .orElseThrow(() -> new GenericExeption("El área no tiene cupo para este insumo."));
        EmpleadosEntity usuario = empleadoActual();
        PartidaEntity p = PartidaEntity.builder()
                .folio(String.format("IDI-%05d", partidaRepository.siguienteFolio()))
                .fecha(ahora()).area(area).insumo(cupo.getInsumo()).cantidad(dto.cantidad())
                .concepto(dto.concepto()).comentario(texto(dto.comentario()))
                .ruta(ruta(dto.rutaId())).usuario(usuario).descontado(false).build();
        aplicarRotoPor(p, dto.concepto(), dto.rotoPor());
        validarCupoLibre(cupo, hoy(), dto.cantidad(), null);
        return toPartida(partidaRepository.save(p));
    }

    /**
     * Solo sin firma y del mismo día. Las partidas anteriores al 27-11 ya se habían descontado al
     * guardarse: en esas se regresa lo anterior al cupo/almacén y se descuenta lo nuevo, como antes.
     */
    @Transactional
    public Partida editarPartida(UUID id, PartidaEditar dto) {
        PartidaEntity p = partida(id);
        if (p.getIdImagenFirma() != null) throw new GenericExeption("La partida ya está firmada.");
        exigirDeHoy(p, "editar");
        LocalDate dia = p.getFecha().toLocalDate();
        CupoEntity anterior = cupoDe(p.getArea(), p.getInsumo().getId());
        BigDecimal cantidadAnterior = p.getCantidad();
        UUID insumoNuevo = dto.insumoId() != null ? dto.insumoId() : p.getInsumo().getId();
        BigDecimal cantidadNueva = dto.cantidad() != null ? dto.cantidad() : cantidadAnterior;
        validarCantidad(cantidadNueva);
        CupoEntity nuevo = cupoDe(p.getArea(), insumoNuevo);
        if (!p.isDescontado()) {
            validarCupoLibre(nuevo, dia, cantidadNueva, p.getId());
        } else if (nuevo.getId().equals(anterior.getId())) {
            moverCupo(nuevo, dia, cantidadNueva, cantidadAnterior);
        } else {
            moverCupo(anterior, dia, BigDecimal.ZERO, cantidadAnterior);
            moverCupo(nuevo, dia, cantidadNueva, BigDecimal.ZERO);
        }
        p.setInsumo(nuevo.getInsumo());
        p.setCantidad(cantidadNueva);
        if (Boolean.TRUE.equals(dto.quitarRuta())) p.setRuta(null);
        else if (dto.rutaId() != null) p.setRuta(ruta(dto.rutaId()));
        if (dto.comentario() != null) p.setComentario(texto(dto.comentario()));
        ConceptoEntrega concepto = dto.concepto() != null ? dto.concepto() : p.getConcepto();
        String rotoPor = dto.rotoPor() != null ? dto.rotoPor()
                : (p.isRotoPorMaquina() ? MAQUINA : (p.getRotoPor() != null ? p.getRotoPor().getId().toString() : null));
        p.setConcepto(concepto);
        aplicarRotoPor(p, concepto, rotoPor);
        return toPartida(partidaRepository.save(p));
    }

    /**
     * Elimina las partidas sin firma del mismo día. Si ya se habían descontado (anteriores al 27-11),
     * su cantidad regresa al cupo y al almacén; las demás no habían descontado nada.
     */
    @Transactional
    public Eliminadas eliminarPartidas(Ids dto) {
        List<PartidaEntity> elegidas = Optional.ofNullable(dto.ids()).orElse(List.<UUID>of()).stream()
                .map(id -> partidaRepository.findById(id).filter(x -> !x.isDeleted() && x.getIdImagenFirma() == null).orElse(null))
                .filter(Objects::nonNull).toList();
        elegidas.forEach(p -> exigirDeHoy(p, "eliminar"));
        for (PartidaEntity p : elegidas) {
            if (p.isDescontado()) {
                CupoEntity cupo = cupoDe(p.getArea(), p.getInsumo().getId());
                moverCupo(cupo, p.getFecha().toLocalDate(), BigDecimal.ZERO, p.getCantidad());
            }
            p.setDeleted(true);
            partidaRepository.save(p);
        }
        return new Eliminadas(elegidas.size());
    }

    /**
     * Al firmar se descuenta del cupo del día de la partida y de la existencia del almacén. Una partida
     * de un día anterior se puede firmar después; el descuento cuenta al día de la partida.
     */
    @Transactional
    public Partida firmarPartida(UUID id, MultipartFile archivo, String idImagenFirma) {
        PartidaEntity p = partida(id);
        if (p.getIdImagenFirma() != null) throw new GenericExeption("La partida ya está firmada.");
        if (!p.isDescontado()) {
            CupoEntity cupo = cupoDe(p.getArea(), p.getInsumo().getId());
            descontarAlFirmar(cupo, p.getFecha().toLocalDate(), p.getCantidad());
            p.setDescontado(true);
        }
        p.setIdImagenFirma(guardarFirma(archivo, idImagenFirma));
        p.setFechaFirma(ahora());
        return toPartida(partidaRepository.save(p));
    }

    // ================================================================== solicitudes

    /** Las del rango y, siempre, las abiertas aunque sean de días anteriores. */
    @Transactional(readOnly = true)
    public List<Solicitud> solicitudes(UUID areaId, LocalDate inicio, LocalDate fin, UUID empleadoId, EstatusSolicitud estatus,
                                       String folio) {
        Specification<SolicitudStockEntity> spec = (root, q, cb) -> {
            List<Predicate> ps = new ArrayList<>();
            ps.add(cb.isFalse(root.get("deleted")));
            if (areaId != null) ps.add(cb.equal(root.get("area").get("id"), areaId));
            if (empleadoId != null) ps.add(cb.equal(root.get("solicitadoPor").get("id"), empleadoId));
            String f = texto(folio);
            if (f != null) {
                ps.add(cb.equal(cb.upper(root.get("folio")), f.toUpperCase(Locale.ROOT)));
            } else if (estatus != null) {
                ps.add(cb.equal(root.get("estatus"), estatus));
            } else {
                LocalDate[] r = rango(inicio, fin);
                ps.add(cb.or(
                        root.get("estatus").in(ABIERTAS),
                        cb.and(cb.greaterThanOrEqualTo(root.get("fechaSolicitud"), r[0].atStartOfDay()),
                                cb.lessThan(root.get("fechaSolicitud"), r[1].plusDays(1).atStartOfDay()))));
            }
            return cb.and(ps.toArray(Predicate[]::new));
        };
        return solicitudRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "fechaSolicitud")).stream()
                .map(this::toSolicitud).toList();
    }

    @Transactional
    public Solicitud solicitar(SolicitudCrear dto) {
        DepartamentoEntity area = area(dto.areaId());
        validarCantidad(dto.cantidad());
        CupoEntity cupo = cupoRepository.findFirstByArea_IdAndInsumo_IdAndDeletedFalse(area.getId(), dto.insumoId())
                .orElseThrow(() -> new GenericExeption("El área no tiene tarjeta de este insumo."));
        solicitudRepository.findFirstByArea_IdAndInsumo_IdAndEstatusInAndDeletedFalse(area.getId(), dto.insumoId(), ABIERTAS)
                .ifPresent(s -> {
                    throw new GenericExeption("Ya hay una solicitud en curso (" + s.getFolio() + ").");
                });
        // Solo cuenta lo firmado: lo registrado sin firma aún no confirma que se necesite más stock.
        BigDecimal firmadoLibre = valoresDia(cupo, hoy()).disponible();
        if (firmadoLibre.compareTo(cupo.getMinimo()) > 0) {
            throw new GenericExeption("Se habilita al llegar a " + num(cupo.getMinimo()) + " o menos (solo cuenta lo firmado).");
        }
        SolicitudStockEntity s = solicitudRepository.save(SolicitudStockEntity.builder()
                .folio(String.format("SDS-%05d", solicitudRepository.siguienteFolio()))
                .area(area).insumo(cupo.getInsumo()).almacen(cupo.getAlmacen())
                .cantidadSolicitada(dto.cantidad()).nota(texto(dto.nota()))
                .solicitadoPor(empleadoActual()).fechaSolicitud(ahora())
                .estatus(EstatusSolicitud.SOLICITADO).build());
        notificar(destinatariosAlmacen(s.getAlmacen(), TipoNotificacion.SOLICITUD_STOCK_NUEVA), "Nueva solicitud de stock",
                "El área " + area.getNombre() + " solicitó " + num(s.getCantidadSolicitada()) + " de " + s.getInsumo().getNombre()
                        + " (" + s.getFolio() + ") al " + s.getAlmacen().getNombre() + ".",
                TipoNotificacion.SOLICITUD_STOCK_NUEVA);
        return toSolicitud(s);
    }

    /** En SOLICITADO se editan cantidad y nota; en ACEPTADO, la cantidad recibida. */
    @Transactional
    public Solicitud editarSolicitud(UUID id, SolicitudEditar dto) {
        SolicitudStockEntity s = solicitud(id);
        if (s.getIdImagenFirma() != null) throw new GenericExeption("La solicitud ya está firmada.");
        if (s.getEstatus() == EstatusSolicitud.SOLICITADO) {
            if ((dto.cantidadSolicitada() != null || dto.nota() != null) && fueraDeTiempoParaEditar(s)) {
                throw new GenericExeption("Ya no se puede editar: pasaron 2 minutos desde que se envió.");
            }
            if (dto.cantidadSolicitada() != null) {
                validarCantidad(dto.cantidadSolicitada());
                s.setCantidadSolicitada(dto.cantidadSolicitada());
            }
            if (dto.nota() != null) s.setNota(texto(dto.nota()));
        } else if (s.getEstatus() == EstatusSolicitud.ACEPTADO && dto.cantidadRecibida() != null) {
            validarRecibida(s, dto.cantidadRecibida());
            s.setCantidadRecibida(dto.cantidadRecibida());
        } else {
            throw new GenericExeption("En este estatus la solicitud ya no se puede editar.");
        }
        return toSolicitud(solicitudRepository.save(s));
    }

    @Transactional
    public Solicitud cancelar(UUID id) {
        SolicitudStockEntity s = solicitud(id);
        if (s.getEstatus() != EstatusSolicitud.SOLICITADO) {
            throw new GenericExeption("Solo se puede cancelar una solicitud en estatus Solicitado.");
        }
        s.setEstatus(EstatusSolicitud.CANCELADO);
        return toSolicitud(solicitudRepository.save(s));
    }

    @Transactional
    public Solicitud aceptar(UUID id, Aceptar dto) {
        SolicitudStockEntity s = solicitud(id);
        exigirResponsableAlmacen(s.getAlmacen());
        if (s.getEstatus() != EstatusSolicitud.SOLICITADO) {
            throw new GenericExeption("Solo se aceptan solicitudes en estatus Solicitado.");
        }
        validarCantidad(dto.cantidadAEntregar());
        validarExistencia(s, dto.cantidadAEntregar());
        s.setEstatus(EstatusSolicitud.ACEPTADO);
        s.setCantidadAEntregar(dto.cantidadAEntregar());
        s.setCantidadRecibida(dto.cantidadAEntregar());
        s.setComentario(texto(dto.comentario()));
        s.setConfirmadoPor(empleadoActual());
        s.setFechaConfirmacion(ahora());
        solicitudRepository.save(s);
        notificarRespuesta(s, "El " + s.getAlmacen().getNombre() + " aceptó la solicitud " + s.getFolio() + " de "
                + s.getInsumo().getNombre() + ": entregará " + num(s.getCantidadAEntregar()) + ".");
        return toSolicitud(s);
    }

    @Transactional
    public Solicitud rechazar(UUID id, Rechazar dto) {
        SolicitudStockEntity s = solicitud(id);
        exigirResponsableAlmacen(s.getAlmacen());
        if (s.getEstatus() != EstatusSolicitud.SOLICITADO) {
            throw new GenericExeption("Solo se rechazan solicitudes en estatus Solicitado.");
        }
        String motivo = texto(dto.comentario());
        if (motivo == null) throw new GenericExeption("Escribe el motivo del rechazo.");
        s.setEstatus(EstatusSolicitud.RECHAZADO);
        s.setComentario(motivo);
        s.setConfirmadoPor(empleadoActual());
        s.setFechaConfirmacion(ahora());
        solicitudRepository.save(s);
        notificarRespuesta(s, "El " + s.getAlmacen().getNombre() + " rechazó la solicitud " + s.getFolio() + " de "
                + s.getInsumo().getNombre() + ". Motivo: " + motivo + ".");
        return toSolicitud(s);
    }

    /** Firma de recibido: la cantidad recibida amplía el cupo de hoy y la solicitud queda bloqueada. */
    @Transactional
    public Solicitud recibir(UUID id, BigDecimal cantidadRecibida, MultipartFile archivo, String idImagenFirma) {
        SolicitudStockEntity s = solicitud(id);
        if (s.getEstatus() != EstatusSolicitud.ACEPTADO) {
            throw new GenericExeption("Solo se reciben solicitudes aceptadas.");
        }
        BigDecimal cantidad = cantidadRecibida != null ? cantidadRecibida : s.getCantidadAEntregar();
        validarRecibida(s, cantidad);
        validarExistencia(s, cantidad);
        CupoEntity cupo = cupoDe(s.getArea(), s.getInsumo().getId());
        CupoDiaEntity dia = diaBloqueado(cupo, hoy());
        dia.setAmpliado(dia.getAmpliado().add(cantidad));
        cupoDiaRepository.save(dia);
        s.setCantidadRecibida(cantidad);
        s.setIdImagenFirma(guardarFirma(archivo, idImagenFirma));
        s.setRecibidoPor(empleadoActual());
        s.setFechaRecepcion(ahora());
        s.setEstatus(EstatusSolicitud.RECIBIDO);
        solicitudRepository.save(s);
        // Si recibió menos de lo autorizado no se vuelve a autorizar: se avisa al almacén cuánto faltó.
        if (s.getCantidadAEntregar() != null && cantidad.compareTo(s.getCantidadAEntregar()) < 0) {
            notificar(destinatariosAlmacen(s.getAlmacen(), TipoNotificacion.DOTACION_INCOMPLETA), "Entrega incompleta",
                    "El área " + s.getArea().getNombre() + " recibió " + num(cantidad) + " de " + num(s.getCantidadAEntregar())
                            + " de " + s.getInsumo().getNombre() + " (" + s.getFolio() + ") del " + s.getAlmacen().getNombre()
                            + ": faltaron " + num(s.getCantidadAEntregar().subtract(cantidad)) + ".",
                    TipoNotificacion.DOTACION_INCOMPLETA);
        }
        return toSolicitud(s);
    }

    /** El solicitante no acepta lo entregado: no se suma al cupo, el insumo se queda en el almacén. */
    @Transactional
    public Solicitud declinar(UUID id, Declinar dto) {
        SolicitudStockEntity s = solicitud(id);
        if (s.getEstatus() != EstatusSolicitud.ACEPTADO || s.getIdImagenFirma() != null) {
            throw new GenericExeption("Solo se declina una solicitud aceptada que aún no se firma de recibido.");
        }
        String motivo = texto(dto != null ? dto.motivo() : null);
        s.setEstatus(EstatusSolicitud.DECLINADO);
        s.setComentario(motivo);
        s.setRecibidoPor(empleadoActual());
        s.setFechaRecepcion(ahora());
        solicitudRepository.save(s);
        notificar(destinatariosAlmacen(s.getAlmacen(), TipoNotificacion.DOTACION_INCOMPLETA), "Entrega declinada",
                "El área " + s.getArea().getNombre() + " declinó la entrega de " + s.getInsumo().getNombre()
                        + " (" + s.getFolio() + ") del " + s.getAlmacen().getNombre() + "."
                        + (motivo != null ? " Motivo: " + motivo + "." : ""),
                TipoNotificacion.DOTACION_INCOMPLETA);
        return toSolicitud(s);
    }

    /** Firma de entrega del responsable del almacén (en Aceptado o Recibido; una sola vez). */
    @Transactional
    public Solicitud firmarAlmacen(UUID id, MultipartFile archivo, String idImagenFirma) {
        SolicitudStockEntity s = solicitud(id);
        exigirResponsableAlmacen(s.getAlmacen());
        if (s.getEstatus() != EstatusSolicitud.ACEPTADO && s.getEstatus() != EstatusSolicitud.RECIBIDO) {
            throw new GenericExeption("El almacén firma la entrega de solicitudes aceptadas.");
        }
        if (s.getFirmaAlmacen() != null) throw new GenericExeption("El almacén ya firmó la entrega.");
        s.setFirmaAlmacen(guardarFirma(archivo, idImagenFirma));
        s.setFechaFirmaAlmacen(ahora());
        s.setFirmadoAlmacenPor(empleadoActual());
        return toSolicitud(solicitudRepository.save(s));
    }

    // ================================================================== reglas internas

    /**
     * Mueve el cupo del día y la existencia del almacén: suma {@code agregar} al consumo y regresa
     * {@code regresar}. Valida que lo nuevo no exceda el disponible ni la existencia.
     */
    private void moverCupo(CupoEntity cupo, LocalDate fecha, BigDecimal agregar, BigDecimal regresar) {
        CupoDiaEntity dia = diaBloqueado(cupo, fecha);
        BigDecimal neto = agregar.subtract(regresar);
        if (neto.signum() > 0 && neto.compareTo(dia.disponible()) > 0) {
            BigDecimal libre = dia.disponible().add(regresar);
            throw new GenericExeption("Supera el cupo disponible (" + num(libre) + ").");
        }
        InventarioEntity inv = inventarioRepository.bloquear(cupo.getAlmacen().getId(), cupo.getInsumo().getId())
                .orElseThrow(() -> new GenericExeption("El insumo ya no está en el inventario del " + cupo.getAlmacen().getNombre() + "."));
        BigDecimal existencia = cero(inv.getCantidad());
        if (neto.signum() > 0 && neto.compareTo(existencia) > 0) {
            throw new GenericExeption("El " + cupo.getAlmacen().getNombre() + " solo tiene " + num(existencia) + " de "
                    + cupo.getInsumo().getNombre() + ".");
        }
        dia.setConsumido(dia.getConsumido().add(neto));
        inv.setCantidad(existencia.subtract(neto));
        cupoDiaRepository.save(dia);
        inventarioRepository.save(inv);
    }

    /**
     * Valida que {@code cantidad} quepa en el cupo del día contando lo firmado y lo pendiente de firma
     * (sin la partida {@code excluir}, que se está editando). No mueve nada.
     */
    private void validarCupoLibre(CupoEntity cupo, LocalDate fecha, BigDecimal cantidad, UUID excluir) {
        CupoDiaEntity dia = diaBloqueado(cupo, fecha);
        BigDecimal libre = dia.disponible().subtract(pendiente(cupo, fecha, excluir));
        if (cantidad.compareTo(libre) > 0) {
            throw new GenericExeption("Supera el cupo disponible (" + num(libre.max(BigDecimal.ZERO)) + ").");
        }
    }

    /** Firma: suma al consumo del día de la partida y descuenta la existencia del almacén. */
    private void descontarAlFirmar(CupoEntity cupo, LocalDate fecha, BigDecimal cantidad) {
        CupoDiaEntity dia = diaBloqueado(cupo, fecha);
        InventarioEntity inv = inventarioRepository.bloquear(cupo.getAlmacen().getId(), cupo.getInsumo().getId())
                .orElseThrow(() -> new GenericExeption("El insumo ya no está en el inventario del " + cupo.getAlmacen().getNombre() + "."));
        BigDecimal existencia = cero(inv.getCantidad());
        if (cantidad.compareTo(existencia) > 0) {
            throw new GenericExeption("El " + cupo.getAlmacen().getNombre() + " solo tiene " + num(existencia) + " de "
                    + cupo.getInsumo().getNombre() + ".");
        }
        dia.setConsumido(dia.getConsumido().add(cantidad));
        inv.setCantidad(existencia.subtract(cantidad));
        cupoDiaRepository.save(dia);
        inventarioRepository.save(inv);
    }

    /** Cupo del día con bloqueo; si el día aún no se usa, se crea con la base de la tarjeta. */
    private CupoDiaEntity diaBloqueado(CupoEntity cupo, LocalDate fecha) {
        return cupoDiaRepository.bloquear(cupo.getId(), fecha)
                .orElseGet(() -> cupoDiaRepository.saveAndFlush(nuevoDia(cupo, fecha, cupo.getBase(), BigDecimal.ZERO)));
    }

    /** Cupo del día para mostrar: el guardado o, si el día aún no se usa, la base (sin guardar). */
    private CupoDiaEntity valoresDia(CupoEntity cupo, LocalDate fecha) {
        return cupoDiaRepository.findByCupo_IdAndFecha(cupo.getId(), fecha)
                .orElseGet(() -> nuevoDia(cupo, fecha, cupo.getBase(), BigDecimal.ZERO));
    }

    /** Partidas del día sin firmar que aún no descuentan. */
    private BigDecimal pendiente(CupoEntity cupo, LocalDate fecha, UUID excluir) {
        return cero(partidaRepository.pendienteFirma(cupo.getArea().getId(), cupo.getInsumo().getId(),
                fecha.atStartOfDay(), fecha.plusDays(1).atStartOfDay(), excluir != null ? excluir : SIN_ID));
    }

    /** Las partidas solo se editan y eliminan el mismo día (hora de México). */
    private void exigirDeHoy(PartidaEntity p, String accion) {
        if (!p.getFecha().toLocalDate().equals(hoy())) {
            throw new GenericExeption("La partida " + p.getFolio() + " es de un día anterior: ya no se puede " + accion + ".");
        }
    }

    /** Al aceptar y al recibir: no se entrega más de lo que hay en el almacén. */
    private void validarExistencia(SolicitudStockEntity s, BigDecimal cantidad) {
        BigDecimal existencia = existencia(s.getAlmacen().getId(), s.getInsumo().getId());
        if (cantidad.compareTo(existencia) > 0) {
            throw new GenericExeption("El " + s.getAlmacen().getNombre() + " solo tiene " + num(existencia) + " de "
                    + s.getInsumo().getNombre() + ".");
        }
    }

    private static boolean fueraDeTiempoParaEditar(SolicitudStockEntity s) {
        return s.getFechaSolicitud() != null && s.getFechaSolicitud().plusSeconds(SEGUNDOS_EDITAR_SOLICITUD).isBefore(ahora());
    }

    /**
     * Almacenes que el usuario puede usar al agregar tarjetas: los que tiene a cargo y los de sus áreas.
     * El administrador, o quien no tiene ninguno asignado, puede usar todos (igual que el front).
     */
    private void exigirAlmacenPermitido(AlmacenEntity almacen) {
        MisPermisos p = misPermisos();
        if (p.esAdmin()) return;
        Set<UUID> permitidos = new HashSet<>(p.responsableAlmacen());
        for (UUID areaId : p.responsableArea()) {
            departamentoRepository.findById(areaId)
                    .filter(d -> d.getAlmacen() != null)
                    .ifPresent(d -> permitidos.add(d.getAlmacen().getId()));
        }
        if (!permitidos.isEmpty() && !permitidos.contains(almacen.getId())) {
            throw new AccessDeniedException("No tienes permitido el " + almacen.getNombre() + ".");
        }
    }

    /** «Roto por» solo con CAMBIO_ROTO (obligatorio); firma el empleado que rompió o el usuario. */
    private void aplicarRotoPor(PartidaEntity p, ConceptoEntrega concepto, String rotoPor) {
        p.setRotoPor(null);
        p.setRotoPorMaquina(false);
        p.setFirmante(p.getUsuario());
        if (concepto != ConceptoEntrega.CAMBIO_ROTO) return;
        String valor = texto(rotoPor);
        if (valor == null) throw new GenericExeption("Indica quién lo rompió (empleado o máquina).");
        if (MAQUINA.equalsIgnoreCase(valor)) {
            p.setRotoPorMaquina(true);
            return;
        }
        EmpleadosEntity e;
        try {
            e = empleadosRepository.findById(UUID.fromString(valor)).filter(x -> !x.isDeleted())
                    .orElseThrow(() -> new GenericExeption("No se encontró el empleado que lo rompió."));
        } catch (IllegalArgumentException ex) {
            throw new GenericExeption("«Roto por» no es válido.");
        }
        p.setRotoPor(e);
        p.setFirmante(e);
    }

    /** Siembra las 4 tarjetas por defecto si el área nunca ha tenido tarjetas y tiene almacén. */
    private void sembrarPorDefecto(DepartamentoEntity area) {
        if (area.getAlmacen() == null || cupoRepository.existsByArea_IdAndDeletedFalse(area.getId())) return;
        List<InventarioEntity> inventario = inventarioRepository.delAlmacen(area.getAlmacen().getId());
        for (Object[] def : POR_DEFECTO) {
            Pattern patron = (Pattern) def[0];
            inventario.stream()
                    .filter(i -> patron.matcher(normal(i.getProductoBasicoEntity().getNombre())).find())
                    .findFirst()
                    .ifPresent(i -> cupoRepository.save(CupoEntity.builder()
                            .area(area).almacen(area.getAlmacen()).insumo(i.getProductoBasicoEntity()).porDefecto(true)
                            .base(BigDecimal.valueOf((Integer) def[1])).minimo(BigDecimal.valueOf((Integer) def[2])).build()));
        }
    }

    private void validarBaseMinimo(BigDecimal base, BigDecimal minimo) {
        if (base == null || minimo == null) throw new GenericExeption("Captura la base y el mínimo.");
        if (base.compareTo(BigDecimal.ONE) < 0) throw new GenericExeption("La base debe ser al menos 1.");
        if (minimo.signum() < 0 || minimo.compareTo(base) >= 0) {
            throw new GenericExeption("El mínimo debe ser menor que la base.");
        }
    }

    private void validarCantidad(BigDecimal cantidad) {
        if (cantidad == null || cantidad.signum() <= 0) throw new GenericExeption("La cantidad debe ser mayor a cero.");
    }

    private void validarRecibida(SolicitudStockEntity s, BigDecimal cantidad) {
        if (cantidad == null || cantidad.compareTo(BigDecimal.ONE) < 0
                || (s.getCantidadAEntregar() != null && cantidad.compareTo(s.getCantidadAEntregar()) > 0)) {
            throw new GenericExeption("La cantidad recibida debe ser de 1 a " + num(s.getCantidadAEntregar()) + ".");
        }
    }

    private void exigirResponsableArea(DepartamentoEntity area) {
        if (!misPermisos().responsableArea().contains(area.getId())) {
            throw new GenericExeption("No eres responsable del área " + area.getNombre() + ".");
        }
    }

    private void exigirResponsableAlmacen(AlmacenEntity almacen) {
        if (!misPermisos().responsableAlmacen().contains(almacen.getId())) {
            throw new GenericExeption("No eres responsable del " + almacen.getNombre() + ".");
        }
    }

    /** Responsable del almacén; si no tiene, los roles con el tipo de notificación activo. */
    private List<EmpleadosEntity> destinatariosAlmacen(AlmacenEntity almacen, TipoNotificacion tipo) {
        if (almacen.getResponsable() != null && !almacen.getResponsable().isDeleted()) {
            return List.of(almacen.getResponsable());
        }
        return notificacionService.destinatariosPorTipo(tipo);
    }

    /** A quien solicitó, siempre, y además a los roles con el tipo activado (sin repetir). */
    private void notificarRespuesta(SolicitudStockEntity s, String mensaje) {
        Map<UUID, EmpleadosEntity> para = new LinkedHashMap<>();
        if (s.getSolicitadoPor() != null) para.put(s.getSolicitadoPor().getId(), s.getSolicitadoPor());
        for (EmpleadosEntity e : Optional.ofNullable(notificacionService.destinatariosPorTipo(TipoNotificacion.SOLICITUD_STOCK_RESPUESTA))
                .orElse(List.of())) {
            para.putIfAbsent(e.getId(), e);
        }
        notificar(new ArrayList<>(para.values()), "Respuesta del almacén", mensaje, TipoNotificacion.SOLICITUD_STOCK_RESPUESTA);
    }

    private void notificar(List<EmpleadosEntity> para, String titulo, String mensaje, TipoNotificacion tipo) {
        if (para == null || para.isEmpty()) return;
        LocalDateTime ahora = ahora();
        notificacionService.crearParaUsuarios(para, titulo, mensaje, tipo, null, null, ahora, ahora);
    }

    /** Guarda la imagen de la firma en uploads/ (se ve por /images/{nombre}) o acepta un nombre ya subido. */
    private String guardarFirma(MultipartFile archivo, String idImagenFirma) {
        if (archivo != null && !archivo.isEmpty()) {
            String ext = EXT_FIRMA.get(archivo.getContentType());
            if (ext == null) throw new GenericExeption("La firma debe ser una imagen PNG, JPG o WEBP.");
            try {
                Files.createDirectories(FIRMAS);
                String nombre = UUID.randomUUID() + ext;
                Files.copy(archivo.getInputStream(), FIRMAS.resolve(nombre), StandardCopyOption.REPLACE_EXISTING);
                return nombre;
            } catch (IOException e) {
                throw new GenericExeption("No se pudo guardar la firma.");
            }
        }
        String nombre = texto(idImagenFirma);
        if (nombre == null || !NOMBRE_ARCHIVO.matcher(nombre).matches()) {
            throw new GenericExeption("Falta la firma.");
        }
        return nombre;
    }

    // ================================================================== consultas y mapeos

    private boolean confirmado(UUID areaId, LocalDate fecha) {
        return confirmacionRepository.findByArea_IdAndFechaAndDeletedFalse(areaId, fecha).isPresent();
    }

    private BigDecimal existencia(UUID almacenId, UUID insumoId) {
        return inventarioRepository.existencia(almacenId, insumoId).map(i -> cero(i.getCantidad())).orElse(BigDecimal.ZERO);
    }

    private CupoDiaEntity nuevoDia(CupoEntity cupo, LocalDate fecha, BigDecimal inicial, BigDecimal faltante) {
        return CupoDiaEntity.builder().cupo(cupo).fecha(fecha).inicial(inicial).ampliado(BigDecimal.ZERO)
                .consumido(BigDecimal.ZERO).faltante(faltante.max(BigDecimal.ZERO)).build();
    }

    /** consumido = lo firmado; disponible descuenta también lo pendiente de firma; existencia = la real. */
    private Cupo toCupo(CupoEntity c, LocalDate fecha) {
        CupoDiaEntity d = valoresDia(c, fecha);
        BigDecimal pendiente = pendiente(c, fecha, null);
        SolicitudAbierta abierta = solicitudRepository
                .findFirstByArea_IdAndInsumo_IdAndEstatusInAndDeletedFalse(c.getArea().getId(), c.getInsumo().getId(), ABIERTAS)
                .map(s -> new SolicitudAbierta(s.getId(), s.getFolio(), s.getEstatus())).orElse(null);
        return new Cupo(c.getId(), c.getArea().getId(), c.getInsumo().getId(), c.getInsumo().getNombre(), unidad(c.getInsumo()),
                c.getAlmacen().getId(), c.getAlmacen().getNombre(), c.isPorDefecto(), c.getBase(), c.getMinimo(),
                d.getInicial(), d.getAmpliado(), d.getConsumido(), d.disponible().subtract(pendiente),
                d.getFaltante(), existencia(c.getAlmacen().getId(), c.getInsumo().getId()), abierta,
                pendiente, c.getCreadoPor() != null ? c.getCreadoPor().getId() : null, nombre(c.getCreadoPor()),
                c.getInsumo().getIdImagen());
    }

    private Confirmacion toConfirmacion(ConfirmacionEntity c) {
        return new Confirmacion(c.getArea().getId(), c.getFecha(), nombre(c.getConfirmadoPor()), c.getConfirmadoEn());
    }

    private Partida toPartida(PartidaEntity p) {
        String rotoPor = p.isRotoPorMaquina() ? MAQUINA : (p.getRotoPor() != null ? p.getRotoPor().getId().toString() : null);
        String rotoPorNombre = p.isRotoPorMaquina() ? "Máquina" : nombre(p.getRotoPor());
        return new Partida(p.getId(), p.getFolio(), p.getFecha(), p.getArea().getId(),
                p.getRuta() != null ? p.getRuta().getId() : null, p.getRuta() != null ? p.getRuta().getNombre() : null,
                p.getInsumo().getId(), p.getInsumo().getNombre(), p.getCantidad(), p.getConcepto(), p.getComentario(),
                rotoPor, rotoPorNombre,
                p.getUsuario() != null ? p.getUsuario().getId() : null, nombre(p.getUsuario()),
                p.getFirmante() != null ? p.getFirmante().getId() : null, nombre(p.getFirmante()),
                p.getIdImagenFirma(), p.getFechaFirma());
    }

    private Solicitud toSolicitud(SolicitudStockEntity s) {
        return new Solicitud(s.getId(), s.getFolio(), s.getArea().getId(), s.getArea().getNombre(),
                s.getInsumo().getId(), s.getInsumo().getNombre(), unidad(s.getInsumo()),
                s.getAlmacen().getId(), s.getAlmacen().getNombre(), existencia(s.getAlmacen().getId(), s.getInsumo().getId()),
                s.getCantidadSolicitada(), s.getCantidadAEntregar(), s.getCantidadRecibida(), s.getNota(),
                s.getSolicitadoPor() != null ? s.getSolicitadoPor().getId() : null, nombre(s.getSolicitadoPor()),
                s.getFechaSolicitud(), s.getEstatus(), nombre(s.getConfirmadoPor()), s.getFechaConfirmacion(),
                s.getComentario(), s.getFechaRecepcion(), nombre(s.getRecibidoPor()), s.getIdImagenFirma(),
                s.getFirmaAlmacen(), s.getFechaFirmaAlmacen(), nombre(s.getFirmadoAlmacenPor()));
    }

    private Area toArea(DepartamentoEntity d) {
        return new Area(d.getId(), d.getNombre(), d.getAlmacen() != null ? d.getAlmacen().getId() : null,
                d.getAlmacen() != null ? d.getAlmacen().getNombre() : null);
    }

    private DepartamentoEntity area(UUID id) {
        if (id == null) throw new GenericExeption("Elige el área.");
        return departamentoRepository.findById(id).filter(d -> !d.isDeleted())
                .orElseThrow(() -> new IDNotFound("No se encontró el área"));
    }

    private AlmacenEntity almacen(UUID id) {
        if (id == null) throw new GenericExeption("Elige el almacén.");
        return almacenRepository.findById(id).filter(a -> !a.isDeleted())
                .orElseThrow(() -> new IDNotFound("No se encontró el almacén"));
    }

    private CupoEntity cupo(UUID id) {
        return cupoRepository.findById(id).filter(c -> !c.isDeleted())
                .orElseThrow(() -> new IDNotFound("No se encontró la tarjeta"));
    }

    private CupoEntity cupoDe(DepartamentoEntity area, UUID insumoId) {
        return cupoRepository.findFirstByArea_IdAndInsumo_IdAndDeletedFalse(area.getId(), insumoId)
                .orElseThrow(() -> new GenericExeption("El área no tiene cupo para este insumo."));
    }

    private PartidaEntity partida(UUID id) {
        return partidaRepository.findById(id).filter(p -> !p.isDeleted())
                .orElseThrow(() -> new IDNotFound("La partida ya no existe"));
    }

    private SolicitudStockEntity solicitud(UUID id) {
        return solicitudRepository.findById(id).filter(s -> !s.isDeleted())
                .orElseThrow(() -> new IDNotFound("La solicitud ya no existe"));
    }

    private RutasEntity ruta(UUID id) {
        if (id == null) return null;
        return rutasRepository.findById(id).filter(r -> !r.isDeleted())
                .orElseThrow(() -> new IDNotFound("No se encontró la ruta"));
    }

    private EmpleadosEntity empleadoActual() {
        EmpleadosEntity e = EmpleadoContext.getEmpleadoActual();
        if (e == null) throw new GenericExeption("No se encontró el usuario en sesión.");
        return e;
    }

    private static boolean esAdmin(EmpleadosEntity e) {
        return e != null && ("admin".equalsIgnoreCase(e.getNombreUsuario()) || Long.valueOf(0L).equals(e.getIdEmpleado()));
    }

    /** Rango inclusivo por días; por defecto hoy. Si viene invertido se ordena (el front ya lo ordena). */
    private LocalDate[] rango(LocalDate inicio, LocalDate fin) {
        LocalDate a = inicio != null ? inicio : hoy();
        LocalDate b = fin != null ? fin : a;
        return a.isAfter(b) ? new LocalDate[]{b, a} : new LocalDate[]{a, b};
    }

    private static <T> Stream<T> todos(Iterable<T> it) {
        return java.util.stream.StreamSupport.stream(it.spliterator(), false);
    }

    static LocalDate hoy() {
        return LocalDate.now(ZONA);
    }

    static LocalDateTime ahora() {
        return LocalDateTime.now(ZONA);
    }

    private static String unidad(ProductoBasicoEntity p) {
        return p.getUnidadMedidaDefault() != null ? p.getUnidadMedidaDefault().getNombre() : null;
    }

    private static String unidad(InventarioEntity i) {
        String u = unidad(i.getProductoBasicoEntity());
        return u != null ? u : (i.getUnidadDeMedida() != null ? i.getUnidadDeMedida().getNombre() : null);
    }

    static String nombre(EmpleadosEntity e) {
        if (e == null) return null;
        String n = Stream.of(e.getNombre(), e.getPrimerApellido(), e.getSegundoApellido())
                .filter(s -> s != null && !s.isBlank()).map(String::trim).collect(Collectors.joining(" "));
        return n.isBlank() ? e.getNombreUsuario() : n;
    }

    private static BigDecimal cero(BigDecimal v) {
        return v != null ? v : BigDecimal.ZERO;
    }

    private static String num(BigDecimal v) {
        return v == null ? "0" : v.stripTrailingZeros().toPlainString();
    }

    private static String texto(String v) {
        if (v == null) return null;
        String t = v.trim();
        return t.isEmpty() ? null : t;
    }

    private static String normal(String s) {
        return s == null ? "" : Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{M}", "").toLowerCase(Locale.ROOT);
    }
}
