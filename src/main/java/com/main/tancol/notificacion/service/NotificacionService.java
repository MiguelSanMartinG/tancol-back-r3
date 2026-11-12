package com.main.tancol.notificacion.service;

import com.main.tancol.common.exeptions.IDNotFound;
import com.main.tancol.empleados.entities.EmpleadosEntity;
import com.main.tancol.empleados.helpers.EmpleadoContext;
import com.main.tancol.notificacion.dto.request.AsignacionNotificacionRolRequestDTO;
import com.main.tancol.notificacion.dto.request.NotificacionTipoUpdateRequestDTO;
import com.main.tancol.notificacion.dto.response.NotificacionResponseDTO;
import com.main.tancol.notificacion.dto.response.NotificacionTipoResponseDTO;
import com.main.tancol.notificacion.entities.NotificacionEntity;
import com.main.tancol.notificacion.entities.NotificacionTipoEntity;
import com.main.tancol.notificacion.entities.RolNotificacionTipoEntity;
import com.main.tancol.notificacion.enums.TipoNotificacion;
import com.main.tancol.notificacion.repositories.NotificacionRepository;
import com.main.tancol.notificacion.repositories.NotificacionTipoRepository;
import com.main.tancol.notificacion.repositories.RolNotificacionTipoRepository;
import com.main.tancol.notificacion.service.interfaces.INotificacionService;
import com.main.tancol.notificacion.util.NotificacionValueMapper;
import com.main.tancol.permisos.entities.RoleEntity;
import com.main.tancol.permisos.repositories.RoleRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class NotificacionService implements INotificacionService {

    /** Nombre exacto del módulo en BD (`modulos.name`). */
    public static final String MODULO_ASIGNACION_EQUIPOS = "Asignacion de equipos";

    /** Nombre exacto del módulo en BD (`modulos.name`). */
    public static final String MODULO_METAS_VENTAS = "Metas de ventas";

    /** Nombre exacto del módulo en BD (`modulos.name`). */
    public static final String MODULO_INSUMOS = "Insumos";

    /** Nombre exacto del módulo en BD (`modulos.name`). */
    public static final String MODULO_BITACORA_VENTA = "Bitácora de Venta";

    /** Nombre exacto del módulo de la campana en BD (`modulos.name`). */
    public static final String MODULO_NOTIFICACIONES = "Notificaciones";

    private final NotificacionRepository repository;
    private final NotificacionTipoRepository tipoRepository;
    private final RolNotificacionTipoRepository asignacionRepository;
    private final RoleRepository roleRepository;

    public NotificacionService(NotificacionRepository repository,
                               NotificacionTipoRepository tipoRepository,
                               RolNotificacionTipoRepository asignacionRepository,
                               RoleRepository roleRepository) {
        this.repository = repository;
        this.tipoRepository = tipoRepository;
        this.asignacionRepository = asignacionRepository;
        this.roleRepository = roleRepository;
    }

    public List<EmpleadosEntity> destinatariosEquiposPrestamo() {
        return destinatariosDe(TipoNotificacion.EQUIPO_MONTO_NO_CUBIERTO);
    }

    public List<EmpleadosEntity> destinatariosMetasVentas() {
        return destinatariosDe(TipoNotificacion.META_VENTA_NO_CUBIERTA);
    }

    public List<EmpleadosEntity> destinatariosInsumos() {
        return destinatariosDe(TipoNotificacion.STOCK_BAJO_MINIMO);
    }

    public List<EmpleadosEntity> destinatariosBitacoraVenta() {
        return destinatariosDe(TipoNotificacion.BOTELLON_ROTO_SOBRE_TOLERANCIA);
    }

    public List<EmpleadosEntity> destinatariosRecepcionOrden() {
        return destinatariosDe(TipoNotificacion.ORDEN_ENVIADA_RECEPCION);
    }

    /**
     * Avisa que una orden de compra pasó a Recepción de compra.
     * No se avisa a quien hizo la acción: esa persona ya ve la confirmación en pantalla.
     */
    @Override
    @Transactional
    public int crearOrdenRecepcion(List<EmpleadosEntity> destinatarios,
                                   String titulo,
                                   String mensaje,
                                   UUID excluirEmpleadoId) {
        if (destinatarios == null || destinatarios.isEmpty()) {
            return 0;
        }
        int creadas = 0;
        for (EmpleadosEntity empleado : destinatarios) {
            if (excluirEmpleadoId != null && excluirEmpleadoId.equals(empleado.getId())) {
                continue;
            }
            repository.save(NotificacionEntity.builder()
                    .titulo(titulo)
                    .mensaje(mensaje)
                    .tipo(TipoNotificacion.ORDEN_ENVIADA_RECEPCION)
                    .leida(false)
                    .empleado(empleado)
                    .build());
            creadas++;
        }
        return creadas;
    }

    @Transactional(readOnly = true)
    public List<NotificacionTipoResponseDTO> listarTipos() {
        return tipoRepository.findAllByDeletedFalseOrderByNombreAsc().stream()
                .map(this::toTipoResponse)
                .toList();
    }

    @Transactional
    public NotificacionTipoResponseDTO actualizarTipo(String codigo, NotificacionTipoUpdateRequestDTO dto) {
        NotificacionTipoEntity tipo = tipoRepository.findByCodigoAndDeletedFalse(normalizarCodigo(codigo))
                .orElseThrow(() -> new IDNotFound("No se encontró el tipo de notificación"));
        tipo.setNombre(dto.getNombre().trim());
        tipo.setDescripcion(StringUtils.trimToNull(dto.getDescripcion()));
        return toTipoResponse(tipoRepository.save(tipo));
    }

    @Transactional(readOnly = true)
    public List<NotificacionTipoResponseDTO> listarTiposDelRol(Long roleId) {
        requireRol(roleId);
        return asignacionRepository.findActivasByRoleId(roleId).stream()
                .map(asignacion -> toTipoResponse(asignacion.getTipo()))
                .toList();
    }

    @Transactional
    public List<NotificacionTipoResponseDTO> reemplazarTiposDelRol(Long roleId, AsignacionNotificacionRolRequestDTO dto) {
        RoleEntity rol = requireRol(roleId);
        Set<String> pedidos = new LinkedHashSet<>();
        Map<String, NotificacionTipoEntity> tipos = new LinkedHashMap<>();
        for (String codigo : dto.getTipos()) {
            String normalizado = normalizarCodigo(codigo);
            if (!pedidos.add(normalizado)) {
                continue;
            }
            tipos.put(normalizado, tipoRepository.findByCodigoAndDeletedFalse(normalizado)
                    .orElseThrow(() -> new IDNotFound("No se encontró el tipo de notificación: " + normalizado)));
        }

        List<RolNotificacionTipoEntity> existentes = asignacionRepository.findAllByRoleId(roleId);
        Map<String, List<RolNotificacionTipoEntity>> porCodigo = new LinkedHashMap<>();
        for (RolNotificacionTipoEntity existente : existentes) {
            if (existente.getTipo().isDeleted()) {
                existente.setDeleted(true);
                existente.setEnabled(false);
                continue;
            }
            porCodigo.computeIfAbsent(existente.getTipo().getCodigo(), key -> new ArrayList<>()).add(existente);
        }

        for (Map.Entry<String, List<RolNotificacionTipoEntity>> entry : porCodigo.entrySet()) {
            boolean conservar = pedidos.remove(entry.getKey());
            boolean activarPrimera = conservar;
            for (RolNotificacionTipoEntity fila : entry.getValue()) {
                if (activarPrimera) {
                    fila.setDeleted(false);
                    fila.setEnabled(true);
                    activarPrimera = false;
                } else {
                    fila.setDeleted(true);
                    fila.setEnabled(false);
                }
            }
        }
        asignacionRepository.saveAll(existentes);
        asignacionRepository.flush();

        for (String codigo : pedidos) {
            asignacionRepository.save(RolNotificacionTipoEntity.builder()
                    .role(rol)
                    .tipo(tipos.get(codigo))
                    .build());
        }
        return listarTiposDelRol(roleId);
    }

    @Override
    @Transactional
    public int crearParaUsuarios(List<EmpleadosEntity> destinatarios,
                                 String titulo,
                                 String mensaje,
                                 TipoNotificacion tipo,
                                 UUID asignacionId,
                                 UUID clienteId,
                                 LocalDateTime periodoInicio,
                                 LocalDateTime periodoFin) {
        if (destinatarios == null || destinatarios.isEmpty()) {
            return 0;
        }
        int creadas = 0;
        for (EmpleadosEntity empleado : destinatarios) {
            repository.save(NotificacionEntity.builder()
                    .titulo(titulo)
                    .mensaje(mensaje)
                    .tipo(tipo)
                    .leida(false)
                    .empleado(empleado)
                    .asignacionId(asignacionId)
                    .clienteId(clienteId)
                    .periodoInicio(periodoInicio)
                    .periodoFin(periodoFin)
                    .build());
            creadas++;
        }
        return creadas;
    }

    @Override
    @Transactional
    public int crearMetaVenta(List<EmpleadosEntity> destinatarios,
                              String titulo,
                              String mensaje,
                              UUID metaVentaId,
                              LocalDateTime periodoInicio,
                              LocalDateTime periodoFin) {
        if (destinatarios == null || destinatarios.isEmpty()) {
            return 0;
        }
        int creadas = 0;
        for (EmpleadosEntity empleado : destinatarios) {
            repository.save(NotificacionEntity.builder()
                    .titulo(titulo)
                    .mensaje(mensaje)
                    .tipo(TipoNotificacion.META_VENTA_NO_CUBIERTA)
                    .leida(false)
                    .empleado(empleado)
                    .metaVentaId(metaVentaId)
                    .periodoInicio(periodoInicio)
                    .periodoFin(periodoFin)
                    .build());
            creadas++;
        }
        return creadas;
    }

    @Override
    @Transactional
    public int crearStockMinimo(List<EmpleadosEntity> destinatarios,
                                String titulo,
                                String mensaje,
                                UUID productoId) {
        if (destinatarios == null || destinatarios.isEmpty()) {
            return 0;
        }
        int creadas = 0;
        for (EmpleadosEntity empleado : destinatarios) {
            repository.save(NotificacionEntity.builder()
                    .titulo(titulo)
                    .mensaje(mensaje)
                    .tipo(TipoNotificacion.STOCK_BAJO_MINIMO)
                    .leida(false)
                    .empleado(empleado)
                    .productoId(productoId)
                    .build());
            creadas++;
        }
        return creadas;
    }

    @Transactional
    public int crearBotellonRoto(List<EmpleadosEntity> destinatarios,
                                 String titulo,
                                 String mensaje,
                                 UUID empleadoEvaluadoId,
                                 UUID productoId,
                                 LocalDateTime periodoInicio,
                                 LocalDateTime periodoFin) {
        if (destinatarios == null || destinatarios.isEmpty()) {
            return 0;
        }
        if (repository.existsByEmpleadoEvaluadoIdAndProductoIdAndTipoAndPeriodoInicioAndLeidaFalseAndDeletedFalse(
                empleadoEvaluadoId, productoId, TipoNotificacion.BOTELLON_ROTO_SOBRE_TOLERANCIA, periodoInicio)) {
            return 0;
        }
        int creadas = 0;
        for (EmpleadosEntity empleado : destinatarios) {
            repository.save(NotificacionEntity.builder()
                    .titulo(titulo)
                    .mensaje(mensaje)
                    .tipo(TipoNotificacion.BOTELLON_ROTO_SOBRE_TOLERANCIA)
                    .leida(false)
                    .empleado(empleado)
                    .productoId(productoId)
                    .empleadoEvaluadoId(empleadoEvaluadoId)
                    .periodoInicio(periodoInicio)
                    .periodoFin(periodoFin)
                    .build());
            creadas++;
        }
        return creadas;
    }

    public boolean hayAlertaStockMinimoPendiente(UUID productoId) {
        return repository.existsByProductoIdAndTipoAndLeidaFalseAndDeletedFalse(
                productoId, TipoNotificacion.STOCK_BAJO_MINIMO);
    }

    @Transactional
    public int desactivarStockMinimo(UUID productoId) {
        List<NotificacionEntity> pendientes = repository
                .findByProductoIdAndTipoAndLeidaFalseAndDeletedFalse(productoId, TipoNotificacion.STOCK_BAJO_MINIMO);
        for (NotificacionEntity notificacion : pendientes) {
            notificacion.setDeleted(true);
        }
        return pendientes.size();
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificacionResponseDTO> listarDelEmpleadoActual() {
        EmpleadosEntity empleado = requireEmpleadoActual();
        Set<TipoNotificacion> permitidos = tiposVisibles(empleado);
        if (permitidos.isEmpty()) {
            return List.of();
        }
        Map<String, String> descripciones = descripciones();
        return repository.findByEmpleadoIdAndDeletedFalseAndTipoInOrderByCreatedAtDesc(empleado.getId(), permitidos)
                .stream()
                .map(notificacion -> NotificacionValueMapper.entityToResponse(
                        notificacion, descripciones.get(notificacion.getTipo().name())))
                .toList();
    }

    @Override
    @Transactional
    public NotificacionResponseDTO marcarLeida(UUID id) {
        EmpleadosEntity empleado = requireEmpleadoActual();
        NotificacionEntity notificacion = repository.findById(id)
                .orElseThrow(() -> new IDNotFound("No se encontró la notificación"));
        if (notificacion.isDeleted()
                || !notificacion.getEmpleado().getId().equals(empleado.getId())
                || !tiposVisibles(empleado).contains(notificacion.getTipo())) {
            throw new IDNotFound("No se encontró la notificación");
        }
        notificacion.setLeida(true);
        return toResponse(repository.save(notificacion));
    }

    @Override
    @Transactional
    public NotificacionResponseDTO crearDebug(String titulo, String mensaje) {
        EmpleadosEntity empleado = requireEmpleadoActual();

        LocalDateTime ahora = LocalDateTime.now();
        String tituloFinal = (titulo == null || titulo.isBlank())
                ? "Notificación de debug"
                : titulo;
        String mensajeFinal = (mensaje == null || mensaje.isBlank())
                ? "Notificación de prueba generada a las " + ahora
                : mensaje;

        NotificacionEntity entity = repository.save(NotificacionEntity.builder()
                .titulo(tituloFinal)
                .mensaje(mensajeFinal)
                .tipo(TipoNotificacion.EQUIPO_MONTO_NO_CUBIERTO)
                .leida(false)
                .empleado(empleado)
                .asignacionId(null)
                .clienteId(null)
                .periodoInicio(ahora)
                .periodoFin(ahora)
                .build());

        return toResponse(entity);
    }

    private List<EmpleadosEntity> destinatariosDe(TipoNotificacion tipo) {
        return asignacionRepository.findDestinatarios(tipo.name(), MODULO_NOTIFICACIONES);
    }

    private Set<TipoNotificacion> tiposVisibles(EmpleadosEntity empleado) {
        RoleEntity rol = empleado.getRole();
        if (rol == null || rol.getId() == null || rol.isDeleted()) {
            return Set.of();
        }
        if (asignacionRepository.contarLecturaEnModulo(rol.getId(), MODULO_NOTIFICACIONES) == 0) {
            return Set.of();
        }
        Set<TipoNotificacion> permitidos = EnumSet.noneOf(TipoNotificacion.class);
        for (RolNotificacionTipoEntity asignacion : asignacionRepository.findActivasByRoleId(rol.getId())) {
            try {
                permitidos.add(TipoNotificacion.valueOf(asignacion.getTipo().getCodigo()));
            } catch (IllegalArgumentException ignored) {
                // Un código de catálogo que ya no está en el enum no se muestra.
            }
        }
        return permitidos;
    }

    private RoleEntity requireRol(Long roleId) {
        return roleRepository.findById(roleId)
                .filter(rol -> !rol.isDeleted())
                .orElseThrow(() -> new IDNotFound("No se encontró el rol"));
    }

    private EmpleadosEntity requireEmpleadoActual() {
        EmpleadosEntity empleado = EmpleadoContext.getEmpleadoActual();
        if (empleado == null) {
            throw new IllegalArgumentException("No se encontró el usuario autenticado");
        }
        return empleado;
    }

    private String normalizarCodigo(String codigo) {
        if (StringUtils.isBlank(codigo)) {
            throw new IllegalArgumentException("El código de notificación es obligatorio");
        }
        String normalizado = codigo.trim().toUpperCase();
        try {
            TipoNotificacion.valueOf(normalizado);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Tipo de notificación no válido: " + codigo);
        }
        return normalizado;
    }

    private Map<String, String> descripciones() {
        Map<String, String> descripciones = new LinkedHashMap<>();
        for (NotificacionTipoEntity tipo : tipoRepository.findAllByDeletedFalseOrderByNombreAsc()) {
            descripciones.put(tipo.getCodigo(), tipo.getDescripcion());
        }
        return descripciones;
    }

    private NotificacionResponseDTO toResponse(NotificacionEntity entity) {
        String descripcion = entity.getTipo() == null
                ? null
                : descripciones().get(entity.getTipo().name());
        return NotificacionValueMapper.entityToResponse(entity, descripcion);
    }

    private NotificacionTipoResponseDTO toTipoResponse(NotificacionTipoEntity tipo) {
        return NotificacionTipoResponseDTO.builder()
                .id(tipo.getId())
                .codigo(tipo.getCodigo())
                .nombre(tipo.getNombre())
                .descripcion(tipo.getDescripcion())
                .build();
    }
}
