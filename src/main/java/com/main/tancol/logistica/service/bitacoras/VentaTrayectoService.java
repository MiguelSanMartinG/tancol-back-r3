package com.main.tancol.logistica.service.bitacoras;

import com.main.tancol.almacen.repositories.UnidadesMedidaRepository;
import com.main.tancol.common.exeptions.IDNotFound;
import com.main.tancol.empleados.entities.EmpleadosEntity;
import com.main.tancol.empleados.repositories.EmpleadosRepository;
import com.main.tancol.logistica.dto.request.ProductosFueraEspecificacionRequestDTO;
import com.main.tancol.logistica.dto.request.VentaTrayectoRequestDTO;
import com.main.tancol.logistica.dto.request.partidas.VentasEmpleadoRequestDTO;
import com.main.tancol.logistica.dto.response.VentaTrayectoResponseDTO;
import com.main.tancol.logistica.entities.bitacora.TrayectoEntity;
import com.main.tancol.logistica.entities.bitacora.VentaTrayectoEntity;
import com.main.tancol.logistica.entities.bitacora.comisiones.ProductosFueraEspecificacionEntity;
import com.main.tancol.logistica.entities.bitacora.comisiones.VentasEmpleadoEntity;
import com.main.tancol.logistica.repositories.VentaTrayectoRepository;
import com.main.tancol.logistica.repositories.VentasEmpleadoRepository;
import com.main.tancol.logistica.service.ProductosFueraEspecificacionService;
import com.main.tancol.logistica.service.interfaces.IVentaTrayectoService;
import com.main.tancol.logistica.util.VentaTrayectoSpecification;
import com.main.tancol.logistica.util.VentaTrayectoValueMapper;
import com.main.tancol.producto.entities.comerciales.ProductoComercialEntity;
import com.main.tancol.producto.repositories.ConceptosRepository;
import com.main.tancol.producto.repositories.ProductoComercialRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

;

@Slf4j
@Service
@RequiredArgsConstructor
public class VentaTrayectoService implements IVentaTrayectoService {

    private final VentaTrayectoRepository repository;
    private final ProductoComercialRepository productoComercialRepository;
    private final UnidadesMedidaRepository unidadesMedidaRepository;
    private final VentasEmpleadoRepository ventasEmpleadoRepository;
    private final ProductosFueraEspecificacionService productosFueraEspecificacionService;
    private final VentasEmpleadoService ventasEmpleadoService;
    private final MermaVentaTrayectoService mermaVentaTrayectoService;
    private final ConceptosRepository conceptosRepository;
    private final EmpleadosRepository empleadosRepository;


    @Override
    public VentaTrayectoResponseDTO create(VentaTrayectoRequestDTO dto) {
        return null;
    }

    @Override
    public VentaTrayectoResponseDTO getById(UUID id) {
        return null;
    }

    @Override
    public VentaTrayectoResponseDTO update(UUID id, VentaTrayectoRequestDTO dto) {
        return null;
    }

    @Override
    public VentaTrayectoResponseDTO delete(UUID id) {
        return null;
    }

    @Override
    public List<VentaTrayectoResponseDTO> getAll(Map<String, String> params) {
        Sort sort = null;
        // sort = Sort.by(Sort.Direction.ASC, "createdAt");
        List<VentaTrayectoEntity> list = repository.findAll(filterWithParameters(params), sort);
        return list.stream().map(VentaTrayectoValueMapper::VentaTrayectoEntityToResponse).toList();
    }

    public Specification<VentaTrayectoEntity> filterWithParameters(Map<String, String> params) {
        return new VentaTrayectoSpecification().getSpecificationByFilters(params);
    }

    public TrayectoEntity create(TrayectoEntity viaje, Set<VentaTrayectoRequestDTO> ventaTrayecto) {
        if (ventaTrayecto == null || ventaTrayecto.isEmpty()) {
            return viaje;
        }

        for (VentaTrayectoRequestDTO venta : ventaTrayecto){

            ProductoComercialEntity producto = null;
            if(venta.getIdProducto() != null){
                producto = productoComercialRepository.findById(venta.getIdProducto()).orElseThrow(() -> new IDNotFound("No se encontro producto"));
            }

            VentaTrayectoEntity p = VentaTrayectoEntity.builder()
                    .producto(producto)
                    .cantidad(venta.getCantidadLlegada())
                    .cantidadLlegada(venta.getCantidadLlegada())
                    .cantidadSalida(venta.getCantidadSalida())
                    .huecos(venta.getHuecos())
                    .comisionesEmpleados(new HashSet<>())
                    .productosFueraEspecificacion(new HashSet<>())
                    .mermas(new HashSet<>())
                    .build();

            if(venta.getProductosFueraEspecificacion() != null){
            p = productosFueraEspecificacionService.create(p,venta.getProductosFueraEspecificacion());
            }
            if(venta.getComisionesEmpleado() != null){
            p = ventasEmpleadoService.create(p, venta.getComisionesEmpleado());
            }
            p = mermaVentaTrayectoService.create(p, venta.getMermas());
            viaje.agregarPartida(p);
        }
        return viaje;
    }

    public TrayectoEntity update(TrayectoEntity v, Set<VentaTrayectoRequestDTO> dto) {
        if(dto == null || dto.isEmpty()){
            log.info("BORRADO");
            v.setVentaTrayecto(new HashSet<>());
            return v;
        }

        Set<VentaTrayectoRequestDTO> conjuntoQueSeDebeCrear  = new HashSet<>();
        Set<VentaTrayectoRequestDTO> paraActualizar = new HashSet<>();
        Set<UUID> conjuntoConId = new HashSet<>();

        // Separar nuevos de viejos.
        for (VentaTrayectoRequestDTO venta : dto){
            if(venta.getIdVenta() == null){
                conjuntoQueSeDebeCrear.add(venta);
            }else {
                conjuntoConId.add(venta.getIdVenta());
                paraActualizar.add(venta);
            }
        }

        quitarSobrantes(v, conjuntoConId);
        // Actualizar las existentes
        updateVentas(v, paraActualizar);
        //Agregar los nuevos;
        v = create(v, conjuntoQueSeDebeCrear);
        return v;
    }

    private void updateVentas(TrayectoEntity v, Set<VentaTrayectoRequestDTO> paraActualizar) {
        if (!paraActualizar.isEmpty()) {
            Map<UUID, VentaTrayectoEntity> ventasExistentes = v.getVentaTrayecto().stream()
                    .collect(Collectors.toMap(VentaTrayectoEntity::getIdVenta, Function.identity()));

            for (VentaTrayectoRequestDTO updateDTO : paraActualizar) {
                VentaTrayectoEntity ventaExistente = ventasExistentes.get(updateDTO.getIdVenta());
                if (ventaExistente != null) {
                    ProductoComercialEntity productoC = null;
                    if(updateDTO.getIdProducto() != null){
                     productoC = productoComercialRepository.findById(updateDTO.getIdProducto())
                             .orElseThrow(() -> new IDNotFound("No se encontro producto"));
                    }

                    ventaExistente.setProducto(productoC);
                    ventaExistente.setCantidadLlegada(updateDTO.getCantidadLlegada());
                    ventaExistente.setCantidadSalida(updateDTO.getCantidadSalida());
                    ventaExistente.setCantidad(updateDTO.getCantidadLlegada());
                    ventaExistente.setHuecos(updateDTO.getHuecos());

                    ventaExistente.getComisionesEmpleados().clear();
                    createComiciones(updateDTO, ventaExistente);
                    ventaExistente.getProductosFueraEspecificacion().clear();
                    createFueraEspecificacion(updateDTO, ventaExistente);
                    mermaVentaTrayectoService.replace(ventaExistente, updateDTO.getMermas());

                }
            }
        }
    }

    private void createComiciones(VentaTrayectoRequestDTO updateDTO, VentaTrayectoEntity ventaExistente) {
        if(updateDTO.getComisionesEmpleado() != null){
            for ( VentasEmpleadoRequestDTO ve : updateDTO.getComisionesEmpleado()) {
                EmpleadosEntity empleado = null;
                if(ve.getIdEmpleado() != null){
                    empleado = empleadosRepository.findById(ve.getIdEmpleado())
                            .orElseThrow(() -> new IDNotFound("No se encontro el empleado solicitado"));
                }

                ventaExistente.getComisionesEmpleados().add(VentasEmpleadoEntity.builder()
                        .comision((ve.getCantidad() == null)?BigDecimal.ZERO:ve.getCantidad())
                        .empleado(empleado)
                        .trayectoOrigen(ventaExistente)
                        .build());
            }
        }
    }

    private void createFueraEspecificacion(VentaTrayectoRequestDTO updateDTO, VentaTrayectoEntity ventaExistente) {
        if(updateDTO.getProductosFueraEspecificacion() != null){
            for (ProductosFueraEspecificacionRequestDTO producto : updateDTO.getProductosFueraEspecificacion()) {

                ventaExistente.getProductosFueraEspecificacion().add(ProductosFueraEspecificacionEntity.builder()
                        .cantidad(producto.getCantidad())
                        .concepto(conceptosRepository.findById(producto.getIdConcepto()).orElseThrow(() -> new IDNotFound("No se encontro el concepto")))
                                .ventaOrigen(ventaExistente)
                        .build());
            }
        }
    }

    private static void quitarSobrantes(TrayectoEntity v, Set<UUID> conjuntoConId) {
        v.getVentaTrayecto().removeIf(venta ->
                !conjuntoConId.contains(venta.getIdVenta())
        );
    }
}
