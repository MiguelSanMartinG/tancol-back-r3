package com.main.tancol.logistica.service.bitacoras;

import com.main.tancol.common.exceptions.exeption.GenericExeption;
import com.main.tancol.common.exeptions.IDNotFound;
import com.main.tancol.empleados.entities.EmpleadosEntity;
import com.main.tancol.empleados.repositories.EmpleadosRepository;
import com.main.tancol.liquidacion.service.LiquidacionConteoTrayectoService;
import com.main.tancol.liquidacion.service.LiquidacionService;
import com.main.tancol.logistica.dto.request.TrayectoRequestDTO;
import com.main.tancol.logistica.dto.response.TrayectoResponseDTO;
import com.main.tancol.logistica.entities.bitacora.BitacoraVentaRutaEntity;
import com.main.tancol.logistica.entities.bitacora.TrayectoEntity;
import com.main.tancol.logistica.entities.rutas.RutasEntity;
import com.main.tancol.logistica.entities.rutas.VehiculosEntity;
import com.main.tancol.logistica.enums.EstadoVentaRuta;
import com.main.tancol.logistica.enums.TipoProceso;
import com.main.tancol.logistica.repositories.BitacoraVentaRepository;
import com.main.tancol.logistica.repositories.RutasRepository;
import com.main.tancol.logistica.repositories.TrayectoRepository;
import com.main.tancol.logistica.repositories.VehiculosRepository;
import com.main.tancol.logistica.service.interfaces.ITrayectoService;
import com.main.tancol.logistica.util.TrayectoSpecification;
import com.main.tancol.logistica.util.TrayectoValueMapper;
import com.main.tancol.tabuladorrotos.service.TabuladorBotellonRotoEvaluacionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrayectoService implements ITrayectoService {

    private final TrayectoRepository repository;
    private final BitacoraVentaRepository bitacoraVentaRepository;
    private final RutasRepository rutasRepository;
    private final VehiculosRepository vehiculosRepository;
    private final EmpleadosRepository empleadosRepository;
    private final VentaTrayectoService ventaTrayectoService;
    private final TrayectoRepository trayectoRepository;
    private final LiquidacionService liquidacionService;
    private final LiquidacionConteoTrayectoService conteoTrayectoService;
    private final TabuladorBotellonRotoEvaluacionService tabuladorBotellonRotoEvaluacionService;

    @Override
    @Transactional
    public TrayectoResponseDTO create(TrayectoRequestDTO dto) {

        if(dto.getIdBitacora() == null) {
            throw new IDNotFound("Es necesario el idBitacora");
        }

        BitacoraVentaRutaEntity padre = getBitacoraOrigen(dto.getIdBitacora());
        Container RutaConductorVehiculo = findIdsInPartida(dto);


        TrayectoEntity viaje = TrayectoValueMapper.TrayectoRequestToEntity(dto, RutaConductorVehiculo);
        //Agregar transacciones al viaje dado.
        viaje = ventaTrayectoService.create(viaje, dto.getVentaTrayecto());

        padre.agregarPartida(viaje);
        TrayectoEntity guardado = trayectoRepository.save(viaje);
        aplicarFirmaYLiquidacion(guardado, dto.isAutoGenerar(), false, false, true, false,
                idRuta(guardado), Map.of(), idEmpleado(guardado.getChofer()));
        tabuladorBotellonRotoEvaluacionService.evaluar(guardado);
        return TrayectoValueMapper.TrayectoEntityToResponse(guardado);
    }

    @Override
    public TrayectoResponseDTO getById(UUID id) {
        TrayectoEntity viaje = repository.findById(id).orElseThrow(() -> new IDNotFound(id.toString()));
        return TrayectoValueMapper.TrayectoEntityToResponse(viaje);
    }

    @Override
    @Transactional
    public TrayectoResponseDTO update(UUID id, TrayectoRequestDTO dto) {
//        if(dto.getIdBitacora() == null) throw new IDNotFound("No se puede actualizar sin idBitacora");
        try{
            Container RutaConductorVehiculo = findIdsInPartida(dto);

            TrayectoEntity viaje = repository.findById(id).orElseThrow(() -> new IDNotFound("No se puede actualizar sin idBitacora"));
            boolean estabaFirmado = EstadoVentaRuta.FIRMADO.equals(viaje.getEstadoVentaRuta());
            boolean marcado = Boolean.TRUE.equals(viaje.getMandadoALiquidacion());
            UUID rutaAntes = idRuta(viaje);
            UUID choferAntes = idEmpleado(viaje.getChofer());
            Map<UUID, BigDecimal> conteoAntes = conteoTrayectoService.cantidadesVendidas(viaje);

            TipoProceso tipo = TipoProceso.fromTipoProceso(dto.getTipoProceso());
            if (tipo != null) {
                viaje.setTipoProceso(tipo);
            }

            viaje.setChofer(RutaConductorVehiculo.conductor());
            viaje.setRuta(RutaConductorVehiculo.ruta());
            viaje.setVehiculo(RutaConductorVehiculo.vehiculo());

            viaje.setIdImagenFirma(dto.getIdImagenFirma());
            viaje.setComentario(dto.getComentario());

            viaje = ventaTrayectoService.update(viaje, dto.getVentaTrayecto());
            boolean conteoCambio = conteoTrayectoService.cambioElConteo(conteoAntes, conteoTrayectoService.cantidadesVendidas(viaje));
            boolean rutaCambio = !Objects.equals(rutaAntes, idRuta(viaje));
            aplicarFirmaYLiquidacion(viaje, dto.isAutoGenerar(), estabaFirmado, marcado, conteoCambio, rutaCambio,
                    rutaAntes, conteoAntes, choferAntes);

            TrayectoEntity guardado = repository.save(viaje);
            tabuladorBotellonRotoEvaluacionService.evaluar(guardado);
            return TrayectoValueMapper.TrayectoEntityToResponse(guardado);

        }catch (Exception e){
            log.error("Error al actualizar el trayecto con ID {}: {}", id, e.getMessage(), e);

            throw new GenericExeption(e.getMessage());

        }
    }

    private void aplicarFirmaYLiquidacion(TrayectoEntity viaje,
                                          boolean pasarALiquidacion,
                                          boolean estabaFirmado,
                                          boolean marcado,
                                          boolean conteoCambio,
                                          boolean rutaCambio,
                                          UUID rutaOriginalId,
                                          Map<UUID, BigDecimal> conteoAntes,
                                          UUID choferOriginalId) {
        if (viaje.getIdImagenFirma() == null || viaje.getIdImagenFirma().isBlank()) {
            return;
        }
        log.info("La partida ha sido firmada y finalizada");
        viaje.setFechaFirma(LocalDateTime.now());
        viaje.setEstadoVentaRuta(EstadoVentaRuta.FIRMADO);
        log.info("Se va a pasar a liquidación?: {}", pasarALiquidacion);
        liquidacionService.reflejarTrayecto(
                viaje,
                marcado,
                conteoCambio,
                rutaCambio,
                rutaOriginalId,
                pasarALiquidacion,
                conteoAntes,
                choferOriginalId,
                marcado || estabaFirmado);
    }

    private UUID idRuta(TrayectoEntity viaje) {
        return viaje.getRuta() == null ? null : viaje.getRuta().getId();
    }

    private UUID idEmpleado(EmpleadosEntity empleado) {
        return empleado == null ? null : empleado.getId();
    }

    @Override
    public ResponseEntity<?> delete(UUID id) {
        TrayectoEntity t = repository.findById(id).orElseThrow(() -> new IDNotFound(id.toString()));
        log.info(String.valueOf(t));
        BitacoraVentaRutaEntity origen = bitacoraVentaRepository.findById(t.getBitacoraOrigen().getIdBitacora()).orElseThrow(() -> new IDNotFound(id.toString()));
        origen.removerPartida(t);
        bitacoraVentaRepository.save(origen);

        return ResponseEntity.noContent().build();
    }

    @Override
    public List<TrayectoResponseDTO> getAll(Map<String, String> params) {
        Sort sort;
        sort = Sort.by(Sort.Direction.ASC, "updatedAt");
        List<TrayectoEntity> list = repository.findAll(filterWithParameters(params), sort);
        return list.stream().map(TrayectoValueMapper::TrayectoEntityToResponse).toList();
    }

    public Specification<TrayectoEntity> filterWithParameters(Map<String, String> params) {
        return new TrayectoSpecification().getSpecificationByFilters(params);
    }

    private BitacoraVentaRutaEntity getBitacoraOrigen(UUID id) {
        BitacoraVentaRutaEntity bitacoraPadre = bitacoraVentaRepository.findById(id).orElseThrow(() -> new IDNotFound("Bitacor no encontrada"));
        return bitacoraPadre;
    }

    private Container findIdsInPartida(TrayectoRequestDTO partida) {

        RutasEntity rt = null;
        VehiculosEntity vh = null;
        EmpleadosEntity conductor = null;

        if (partida.getIdRuta() != null) {
            rt = rutasRepository.findById(partida.getIdRuta()).orElseThrow(() -> new IDNotFound("Ruta no encontrada"));
        }
        if (partida.getIdVehiculo() != null) {
            vh = vehiculosRepository.findById(partida.getIdVehiculo()).orElseThrow(() -> new IDNotFound("Vehiculo no encontrada"));
        }
        if (partida.getIdChofer() != null) {
            conductor = empleadosRepository.findById(partida.getIdChofer()).orElseThrow(() -> new IDNotFound("Conductor no encontrada"));
        }

        return new Container(rt, vh, conductor);
    }
    public record Container(RutasEntity ruta, VehiculosEntity vehiculo, EmpleadosEntity conductor) {
    }
}
