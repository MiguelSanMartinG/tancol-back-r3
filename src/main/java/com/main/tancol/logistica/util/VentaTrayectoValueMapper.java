package com.main.tancol.logistica.util;


import com.main.tancol.logistica.dto.request.VentaTrayectoRequestDTO;
import com.main.tancol.logistica.dto.response.VentaTrayectoResponseDTO;
import com.main.tancol.logistica.entities.bitacora.VentaTrayectoEntity;
import com.main.tancol.producto.util.comercial.ProductoComercialValueMapper;
import org.apache.commons.lang3.ObjectUtils;

import java.util.HashSet;
import java.util.stream.Collectors;


public class VentaTrayectoValueMapper {

        public static VentaTrayectoEntity VentaTrayectoRequestToEntity(VentaTrayectoRequestDTO dto) {
        return VentaTrayectoEntity.builder()
                .build();

    }

    public static VentaTrayectoResponseDTO VentaTrayectoEntityToResponse(VentaTrayectoEntity dto){
        return  VentaTrayectoResponseDTO.builder()
                .idVenta(dto.getIdVenta())
                .huecos(dto.getHuecos())
                .cantidadVendida(dto.getCantidad())
                .cantidadSalida(dto.getCantidadSalida())
                .cantidadLlegada(dto.getCantidadLlegada())
                .producto(ObjectUtils.isNotEmpty(dto.getProducto())? ProductoComercialValueMapper.ProductoComercialEntityToResponse(dto.getProducto()): null)
                .productosFueraEspecificacion(ObjectUtils.isNotEmpty(dto.getProductosFueraEspecificacion())
                        ? dto.getProductosFueraEspecificacion().stream().map(ProductosFueraEspecificacionValueMapper::ProductosFueraEspecificacionEntityToResponse).collect(Collectors.toSet())
                        : new HashSet<>())
                .comisionesEmpleados(ObjectUtils.isNotEmpty(dto.getComisionesEmpleados())
                        ? dto.getComisionesEmpleados().stream().map(VentasEmpleadoValueMapper::VentasEmpleadoEntityToResponse).collect(Collectors.toSet())
                        : new HashSet<>())
                .mermas(ObjectUtils.isNotEmpty(dto.getMermas())
                        ? dto.getMermas().stream().map(MermaVentaTrayectoValueMapper::toResponse).collect(Collectors.toSet())
                        : new HashSet<>())
                .updatedAt(dto.getUpdatedAt())
                .createdAt(dto.getCreatedAt())
                .build();
    }


}
