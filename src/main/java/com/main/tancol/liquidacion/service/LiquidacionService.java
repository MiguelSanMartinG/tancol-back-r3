package com.main.tancol.liquidacion.service;

import com.main.tancol.common.exceptions.exeption.GenericExeption;
import com.main.tancol.common.exeptions.IDNotFound;
import com.main.tancol.common.service.FolioService;
import com.main.tancol.empleados.entities.EmpleadosEntity;
import com.main.tancol.empleados.helpers.EmpleadoContext;
import com.main.tancol.liquidacion.dto.request.CancelacionLiquidacionRequestDTO;
import com.main.tancol.liquidacion.dto.request.CancelacionPartidaRequestDTO;
import com.main.tancol.liquidacion.dto.request.LiquidacionRequestDTO;
import com.main.tancol.liquidacion.dto.response.LiquidacionResponseDTO;
import com.main.tancol.liquidacion.dto.response.NotaDTO;
import com.main.tancol.liquidacion.entities.LiquidacionEntity;
import com.main.tancol.liquidacion.entities.LiquidacionPartidaEntity;
import com.main.tancol.liquidacion.enums.EstadosLiquidacion;
import com.main.tancol.liquidacion.repositories.LiquidacionPartidaRepository;
import com.main.tancol.liquidacion.repositories.LiquidacionRepository;
import com.main.tancol.liquidacion.service.interfaces.ILiquidacionService;
import com.main.tancol.liquidacion.util.LiquidacionSpecification;
import com.main.tancol.liquidacion.util.LiquidacionValueMapper;
import com.main.tancol.logistica.entities.bitacora.TrayectoEntity;
import com.main.tancol.logistica.entities.rutas.RutasEntity;
import com.main.tancol.logistica.repositories.RutasRepository;
import com.main.tancol.permisos.enums.AdvancedPermissionCode;
import com.main.tancol.permisos.service.PermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class LiquidacionService implements ILiquidacionService {

    private final LiquidacionRepository repository;
    private final RutasRepository rutasRepository;
    private final FolioService folioService;
    private final LiquidacionPartidaService liquidacionPartidaService;
    private final LiquidacionTotalizadoService liquidacionTotalizadoService;
    private final LiquidacionPartidaRepository liquidacionPartidaRepository;
    private final LiquidacionRebalanceService liquidacionRebalanceService;
    private final LiquidacionConteoTrayectoService conteoTrayectoService;
    private final PermissionService permissionService;

    private static final List<EstadosLiquidacion> ESTADOS_LIQUIDACION_CERRADA =
            List.of(EstadosLiquidacion.FINALIZADA, EstadosLiquidacion.CANCELADA);


    @Override
    @Transactional
    public LiquidacionResponseDTO create(LiquidacionRequestDTO dto) {

        try{
            LiquidacionEntity entity = LiquidacionValueMapper.LiquidacionRequestToEntity(dto);

            RutasEntity ruta;
            if(dto.getIdRuta() != null){
                ruta = rutasRepository.findById(dto.getIdRuta()).orElseThrow(() -> new IDNotFound("Ruta no encontrada"));
                entity.setRuta(ruta);
            }
            entity.setOrigenBitacora(false);
            entity.setLiquidacionPartida(new LinkedList<>());
            entity.setTotalesPorProductos(new LinkedList<>());

            liquidacionPartidaService.create(entity,dto.getPartida());
            liquidacionTotalizadoService.create(entity, dto.getTotalizados());

            entity.setFolio(folioService.generarFolio(2));

            entity = repository.save(entity);

            entity.getTotalesPorProductos().forEach((x) -> {
                if(x == null) return;
                x.getEmpleadoTotalizadoLiquidacion().forEach(
                        (y) -> {
//                        log.info(" - "+ y.getEmpleado().getNombre());
                            if(y.getAyudantes() != null){
                                y.getAyudantes().forEach(z -> {
//                                log.info(" --- " + z.getEmpleado().getNombre());
                                });
                            }
                        }
                );
            });

            return LiquidacionValueMapper.LiquidacionEntityToResponse(entity);

        }catch (Exception e) {
            log.error("Error inesperado al crear liquidación", e);
            throw new RuntimeException("Error al procesar la creación de la liquidación", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public LiquidacionResponseDTO getById(UUID id) {
        LiquidacionEntity l = this.repository.findById(id).orElseThrow(() ->
                new IDNotFound("Liquidación no encontrada"));
        l.getTotalesPorProductos().forEach((x) -> {
            if(x == null) return;
//            log.info("----------------");
            x.getEmpleadoTotalizadoLiquidacion().forEach(
                    (y) -> {
//                        log.info(" - "+ y.getEmpleado().getNombre());
                        if(y.getAyudantes() != null){
                            y.getAyudantes().forEach(z -> {
//                                log.info(" --- " + z.getEmpleado().getNombre());
                            });
                        }
                    }
            );
        });

        return LiquidacionValueMapper.LiquidacionEntityToResponse(l);
    }

    @Override
    @Transactional
    public LiquidacionResponseDTO update(UUID id, LiquidacionRequestDTO dto) {

        LiquidacionEntity liquidacion = this.repository.findById(id).orElseThrow(() -> new IDNotFound("Liquidación no encontrada"));;
        validarLiquidacionModificable(liquidacion);
        updateLiquidacion(liquidacion, dto);
        this.liquidacionPartidaService.update(liquidacion, dto.getPartida());
        this.liquidacionTotalizadoService.update(liquidacion, dto.getTotalizados());

        return LiquidacionValueMapper.LiquidacionEntityToResponse(this.repository.save(liquidacion));
    }

    private void updateLiquidacion(LiquidacionEntity liquidacion, LiquidacionRequestDTO dto) {
        RutasEntity route = null;
        if(dto.getIdRuta() != null){
            route = rutasRepository.findById(dto.getIdRuta()).orElseThrow(() -> new IDNotFound("Ruta no encontrada"));
        }
        liquidacion.setRuta(route);
        if(!dto.getFecha().isEqual(liquidacion.getFecha())){
        liquidacion.setFecha(dto.getFecha());
        }
        if(dto.getEstado() != null){
        liquidacion.setEstadosLiquidacion(dto.getEstado());
        }
        liquidacion.setBloqueNota(liquidacion.getBloqueNota());
        liquidacion.setObservaciones(dto.getObservaciones());
        liquidacion.setEstadosLiquidacion(liquidacion.getEstadosLiquidacion());

    }

    @Override
    public LiquidacionResponseDTO delete(UUID id) {
        this.repository.deleteById(id);
        return null;
    }

    @Override
    @Transactional
    public LiquidacionResponseDTO cancelarPartida(UUID liquidacionId, UUID partidaId, CancelacionPartidaRequestDTO dto) {
        assertPuedeCancelarLiquidacion();
        LiquidacionEntity liquidacion = repository.findById(liquidacionId)
                .orElseThrow(() -> new IDNotFound("Liquidación no encontrada"));
        validarLiquidacionModificable(liquidacion);

        LiquidacionPartidaEntity partida = liquidacion.getLiquidacionPartida().stream()
                .filter(p -> partidaId.equals(p.getIdPartida()))
                .findFirst()
                .orElseThrow(() -> new IDNotFound("Partida no encontrada en la liquidación"));

        if (partida.isCancelada()) {
            throw new GenericExeption("La partida ya está cancelada");
        }

        aplicarCancelacionPartida(partida, dto.getMotivo());

        if (dto.getNuevaPartida() != null) {
            liquidacionPartidaService.create(liquidacion, List.of(dto.getNuevaPartida()));
            LiquidacionPartidaEntity nueva = liquidacion.getLiquidacionPartida()
                    .get(liquidacion.getLiquidacionPartida().size() - 1);
            liquidacionRebalanceService.rebalancePorReemplazoPartida(liquidacion, partida, nueva);
        } else {
            liquidacionRebalanceService.rebalancePorCancelacionPartida(liquidacion, partida);
        }

        return LiquidacionValueMapper.LiquidacionEntityToResponse(repository.save(liquidacion));
    }

    @Override
    @Transactional
    public LiquidacionResponseDTO cancelarLiquidacion(UUID id, CancelacionLiquidacionRequestDTO dto) {
        assertPuedeCancelarLiquidacion();
        LiquidacionEntity liquidacion = repository.findById(id)
                .orElseThrow(() -> new IDNotFound("Liquidación no encontrada"));

        if (EstadosLiquidacion.CANCELADA.equals(liquidacion.getEstadosLiquidacion())) {
            throw new GenericExeption("La liquidación ya está cancelada");
        }

        DatosCancelacion datos = resolverDatosCancelacion(dto.getMotivo());
        liquidacion.getLiquidacionPartida().stream()
                .filter(p -> !p.isCancelada())
                .forEach(partida -> aplicarCancelacionPartida(partida, datos));
        aplicarCancelacionLiquidacion(liquidacion, datos);
        liquidacionRebalanceService.limpiarTotalizados(liquidacion);
        liquidacion.setEstadosLiquidacion(EstadosLiquidacion.CANCELADA);

        return LiquidacionValueMapper.LiquidacionEntityToResponse(repository.save(liquidacion));
    }

    private void aplicarCancelacionPartida(LiquidacionPartidaEntity partida, String motivo) {
        aplicarCancelacionPartida(partida, resolverDatosCancelacion(motivo));
    }

    private void aplicarCancelacionPartida(LiquidacionPartidaEntity partida, DatosCancelacion datos) {
        partida.setCancelada(true);
        partida.setMotivoCancelacion(datos.motivo());
        partida.setUsuarioCancelacion(datos.usuario());
        partida.setFechaCancelacion(datos.fecha());
        partida.calcularImporte();
    }

    private void aplicarCancelacionLiquidacion(LiquidacionEntity liquidacion, DatosCancelacion datos) {
        liquidacion.setMotivoCancelacion(datos.motivo());
        liquidacion.setUsuarioCancelacion(datos.usuario());
        liquidacion.setFechaCancelacion(datos.fecha());
    }

    private DatosCancelacion resolverDatosCancelacion(String motivo) {
        EmpleadosEntity empleado = EmpleadoContext.getEmpleadoActual();
        if (ObjectUtils.isEmpty(empleado)) {
            throw new IllegalArgumentException("No se encontró el usuario autenticado");
        }
        return new DatosCancelacion(
                motivo.trim(),
                empleado.getNombreUsuario(),
                LocalDateTime.now()
        );
    }

    private record DatosCancelacion(String motivo, String usuario, LocalDateTime fecha) {}

    private void validarLiquidacionModificable(LiquidacionEntity liquidacion) {
        if (EstadosLiquidacion.CANCELADA.equals(liquidacion.getEstadosLiquidacion())) {
            throw new GenericExeption("No se puede modificar una liquidación cancelada");
        }
    }

    private void assertPuedeCancelarLiquidacion() {
        EmpleadosEntity empleado = EmpleadoContext.getEmpleadoActual();
        if (!permissionService.hasAdvancedPermission(
                empleado, AdvancedPermissionCode.CANCELAR_LIQUIDACION.name())) {
            throw new AccessDeniedException("No tiene permiso para cancelar liquidaciones");
        }
    }

    @Override
    public List<LiquidacionResponseDTO> getAll(Map<String, String> params) {
//        Sort sort = Sort.unsorted();
         Sort sort = Sort.by(Sort.Direction.ASC, "createdAt");
        List<LiquidacionEntity> list = repository.findAll(filterWithParameters(params), sort);
        return list.stream().map(LiquidacionValueMapper::LiquidacionEntityToResponse).toList();
    }

    public Specification<LiquidacionEntity> filterWithParameters(Map<String, String> params) {
        return new LiquidacionSpecification().getSpecificationByFilters(params);
    }

    public NotaDTO notaMasAlta(String id) {
        Long folio = 0L;
        Optional<LiquidacionPartidaEntity> last = liquidacionPartidaRepository.findFirstByLiquidacionOrigen_BloqueNotaAndFolioNotNullOrderByFolioDesc(id);
         if(last.isPresent()){
             folio = last.get().getFolio();
         }
        return NotaDTO.builder().lastFolio(folio).build();
    }

    @Transactional
    public void reflejarTrayecto(TrayectoEntity viaje,
                                 boolean marcado,
                                 boolean conteoCambio,
                                 boolean rutaCambio,
                                 UUID rutaOriginalId,
                                 boolean pasarALiquidacion,
                                 Map<UUID, BigDecimal> anteriores,
                                 UUID choferOriginalId,
                                 boolean buscarLegacy) {
        if (viaje.getBitacoraOrigen() == null || viaje.getBitacoraOrigen().getFecha() == null) {
            throw new GenericExeption("La bitácora del trayecto no tiene fecha");
        }
        UUID rutaConsulta = rutaOriginalId != null
                ? rutaOriginalId
                : (viaje.getRuta() == null ? null : viaje.getRuta().getId());
        if (rutaConsulta == null) {
            throw new GenericExeption("El trayecto no tiene ruta");
        }

        LocalDateTime inicio = viaje.getBitacoraOrigen().getFecha().atStartOfDay();
        LocalDateTime fin = inicio.plusDays(1);
        List<LiquidacionEntity> delDia = repository.findByFechaBetweenAndRuta_Id(inicio, fin, rutaConsulta);
        if (delDia == null) {
            delDia = List.of();
        }

        LiquidacionConteoTrayectoService.Rastro rastro = (marcado || buscarLegacy)
                ? conteoTrayectoService.rastrear(delDia, viaje, anteriores, choferOriginalId)
                : LiquidacionConteoTrayectoService.Rastro.ninguno();

        boolean yaEnviado = marcado || rastro.yaEnviado();
        conteoTrayectoService.rechazarCambioDeRuta(rutaCambio, yaEnviado);

        if (!yaEnviado) {
            if (!buscarLegacy) {
                if (pasarALiquidacion) {
                    procesarTrayecto(viaje);
                    viaje.setMandadoALiquidacion(true);
                }
                return;
            }
            if (!conteoCambio) {
                return;
            }
            if (delDia.isEmpty()) {
                if (pasarALiquidacion) {
                    procesarTrayecto(viaje);
                    viaje.setMandadoALiquidacion(true);
                }
                return;
            }
            boolean hayAbierta = delDia.stream()
                    .anyMatch(liquidacion -> !conteoTrayectoService.estaCerrada(liquidacion.getEstadosLiquidacion()));
            throw new GenericExeption(hayAbierta
                    ? LiquidacionConteoTrayectoService.MSG_NO_ENCONTRADO
                    : LiquidacionConteoTrayectoService.MSG_CERRADA);
        }

        if (rastro.tipo() == LiquidacionConteoTrayectoService.TipoRastro.AMBIGUO) {
            if (conteoCambio) {
                throw new GenericExeption(LiquidacionConteoTrayectoService.MSG_AMBIGUO);
            }
            return;
        }
        if (rastro.tipo() == LiquidacionConteoTrayectoService.TipoRastro.INCOMPLETO) {
            if (conteoCambio) {
                throw new GenericExeption(LiquidacionConteoTrayectoService.MSG_NO_ENCONTRADO);
            }
            return;
        }

        LiquidacionEntity destino = rastro.liquidacion();
        if ((rastro.tipo() == LiquidacionConteoTrayectoService.TipoRastro.NINGUNO) && marcado) {
            destino = delDia.stream()
                    .filter(liquidacion -> !conteoTrayectoService.estaCerrada(liquidacion.getEstadosLiquidacion()))
                    .findFirst()
                    .orElse(null);
        }

        if (destino == null || conteoTrayectoService.estaCerrada(destino.getEstadosLiquidacion())) {
            if (!conteoCambio) {
                viaje.setMandadoALiquidacion(true);
                return;
            }
            boolean hayCerrada = delDia.stream()
                    .anyMatch(liquidacion -> conteoTrayectoService.estaCerrada(liquidacion.getEstadosLiquidacion()));
            throw new GenericExeption(hayCerrada
                    ? LiquidacionConteoTrayectoService.MSG_CERRADA
                    : LiquidacionConteoTrayectoService.MSG_NO_ENCONTRADO);
        }

        conteoTrayectoService.sincronizar(destino, viaje, anteriores, choferOriginalId);
        viaje.setMandadoALiquidacion(true);
        repository.save(destino);
    }

    public void procesarTrayecto(TrayectoEntity viaje) {
        LocalDateTime fechaInicio = viaje.getBitacoraOrigen().getFecha().atStartOfDay();
        LocalDateTime fechaFin = fechaInicio.plusDays(1);

        boolean existeYaUna = repository.existsByFechaBetweenAndRuta_IdAndEstadosLiquidacionNotIn(
                fechaInicio, fechaFin, viaje.getRuta().getId(), ESTADOS_LIQUIDACION_CERRADA);
        if (!existeYaUna) {
            // Por partida
//            log.info("Creando liquidacion");
            LiquidacionEntity newL = LiquidacionEntity.builder()
                    .fecha(viaje.getBitacoraOrigen().getFecha().atTime(8,0))
                    .bloqueNota(viaje.getRuta().getLetraPredeterminada())
                    .estadosLiquidacion(EstadosLiquidacion.PENDIENTE)
                    .folio(folioService.generarFolio(2))
                    .totalesPorProductos(new LinkedList<>())
                    .liquidacionPartida(new LinkedList<>())
                    .ruta(viaje.getRuta())
                    .origenBitacora(true)
                    .build();

            //POR VIAJE
            poblarLiquidacion(viaje, newL);

        }
        else {
//            log.info("Se encontro una liquidacion, se anexara...");
            LiquidacionEntity liquidacion = repository.findByFechaBetweenAndRuta_IdAndEstadosLiquidacionNotIn(
                    fechaInicio, fechaFin, viaje.getRuta().getId(), ESTADOS_LIQUIDACION_CERRADA);
            if(ObjectUtils.isEmpty(liquidacion)) throw new IDNotFound("No se encontro una liquidacion algo paso mal...");
            poblarLiquidacion(viaje, liquidacion);
        }

    }

    @Transactional
    public void poblarLiquidacion(TrayectoEntity viaje, LiquidacionEntity newL) {

        // Validaciones
        if (viaje.getChofer() == null) {
            throw new IllegalArgumentException("El viaje debe tener un chofer asignado");
        }

        conteoTrayectoService.incorporarTrayecto(newL, viaje);
        newL.setOrigenBitacora(true);
        this.repository.save(newL);
    }


}
