package com.main.tancol.logistica.util;


import com.main.tancol.empleados.util.EmpleadosValueMapper;
import com.main.tancol.logistica.dto.request.TrayectoRequestDTO;
import com.main.tancol.logistica.dto.response.TrayectoResponseDTO;
import com.main.tancol.logistica.entities.bitacora.TrayectoEntity;
import com.main.tancol.logistica.enums.EstadoVentaRuta;
import com.main.tancol.logistica.enums.TipoProceso;
import com.main.tancol.logistica.service.bitacoras.TrayectoService;
import com.main.tancol.logistica.util.ruta.RutasValueMapper;
import org.apache.commons.lang3.ObjectUtils;

import java.util.HashSet;
import java.util.stream.Collectors;

public class TrayectoValueMapper {




        public static TrayectoEntity TrayectoRequestToEntity(TrayectoRequestDTO dto, TrayectoService.Container rutaConductorVehiculo) {
            TipoProceso tipo = TipoProceso.fromTipoProcesoOrSinProceso(dto.getTipoProceso());

        return TrayectoEntity.builder()
                .ruta(rutaConductorVehiculo.ruta())
                .chofer(rutaConductorVehiculo.conductor())
                .vehiculo(rutaConductorVehiculo.vehiculo())
                .tipoProceso(tipo)
                .idImagenFirma(dto.getIdImagenFirma().isBlank() ? null : dto.getIdImagenFirma())
                .estadoVentaRuta(EstadoVentaRuta.PENDIENTE)
                .ventaTrayecto(new HashSet<>())
                .comentario(dto.getComentario())
                .build();

    }

    public static TrayectoResponseDTO TrayectoEntityToResponse(TrayectoEntity dto){
        return  TrayectoResponseDTO.builder()
                .conductor(ObjectUtils.isNotEmpty(dto.getChofer())? EmpleadosValueMapper.EmpleadosEntityToResponse(dto.getChofer()): null)
                .idPartida(dto.getIdTrayecto())
                .ruta(ObjectUtils.isNotEmpty(dto.getRuta())? RutasValueMapper.RutasEntityToResponse(dto.getRuta()) : null)
                .vehiculo(ObjectUtils.isNotEmpty(dto.getVehiculo())? VehiculosValueMapper.VehiculosEntityToResponse(dto.getVehiculo()) : null)
                .estadoVentaRuta(dto.getEstadoVentaRuta())
                .idImagenFirma(dto.getIdImagenFirma())
                .comentario(dto.getComentario())
                .ventaTrayecto(ObjectUtils.isNotEmpty(dto.getVentaTrayecto())
                        ? dto.getVentaTrayecto().stream().map(VentaTrayectoValueMapper::VentaTrayectoEntityToResponse).collect(Collectors.toSet())
                        : new HashSet<>())
                .tipoProceso(ObjectUtils.isNotEmpty(dto.getTipoProceso()) ? dto.getTipoProceso() : TipoProceso.SIN_PROCESO)
                .fechaFirma(dto.getFechaFirma())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .build();

        //        return  BitacoraPartidaVentasResponseDTO.builder()
//                .cantidadLlegada(dto.getCantidadLlegada())
//                .cantidadSalida(dto.getCantidadSalida())
//                .conductor(ObjectUtils.isNotEmpty(dto.getChofer())? EmpleadosValueMapper.EmpleadosEntityToResponse(dto.getConductor()): null)
//                .huecos(dto.getHuecos())
//                .rotos(dto.getRotos())
//                .vendidos(dto.getVendidos())
//                .idPartida(dto.getIdPartidaVenta())
//                .comisionesEmpleados(ObjectUtils.isNotEmpty(dto.getComisionesEmpleados())?  dto.getComisionesEmpleados().stream().map(VentasEmpleadoValueMapper::VentasEmpleadoEntityToResponse).collect(Collectors.toSet()) : null)
//                .productoComercial(ObjectUtils.isNotEmpty(dto.getProductoComercial()) ? ProductoComercialValueMapper.ProductoComercialEntityToResponse(dto.getProductoComercial()): null)
//                .ruta(ObjectUtils.isNotEmpty(dto.getRuta())? RutasValueMapper.RutasEntityToResponse(dto.getRuta()) : null)
//                .vehiculo(ObjectUtils.isNotEmpty(dto.getVehiculo())? VehiculosValueMapper.VehiculosEntityToResponse(dto.getVehiculo()) : null)
//                .estadoVentaRuta(dto.getEstadoVentaRuta())
//                .idImagenFirma(dto.getIdImagenFirma())
//                .comentario(dto.getComentario())
//                .rezago(ObjectUtils.isNotEmpty(dto.getProductosNoConsignados())?
//                        dto.getProductosNoConsignados().stream().map(ProductosNoConsignadosValueMapper::ProductosNoConsignadosEntityToResponse).collect(Collectors.toSet()) : null)
//                .build();
    }
}
