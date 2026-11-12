package com.main.tancol.logistica.util.ruta;

import com.main.tancol.empleados.entities.EmpleadosEntity;
import com.main.tancol.empleados.util.EmpleadosValueMapper;
import com.main.tancol.logistica.dto.request.RutasRequestDTO;
import com.main.tancol.logistica.dto.response.RutasResponseDTO;
import com.main.tancol.logistica.entities.rutas.RutasEntity;
import com.main.tancol.logistica.enums.TipoRuta;
import com.main.tancol.logistica.entities.rutas.VehiculosEntity;
import com.main.tancol.logistica.util.VehiculosValueMapper;
import com.main.tancol.producto.entities.comerciales.ProductoComercialEntity;
import com.main.tancol.producto.util.comercial.ProductoComercialValueMapper;
import org.apache.commons.lang3.ObjectUtils;

import java.util.HashSet;
import java.util.stream.Collectors;

public class RutasValueMapper {

        public static RutasEntity RutasRequestToEntity(RutasRequestDTO dto, EmpleadosEntity chofer, ProductoComercialEntity producto, VehiculosEntity vehiculo) {
        return RutasEntity.builder()
                .nombre(dto.getNombre())
                .numero(dto.getNumero())
                .vehiculo(vehiculo)
                .chofer(chofer)
                .productBase(producto)
                .letraPredeterminada(dto.getLetraPredeterminada())
                .tipoRuta(dto.getTipoRuta() != null ? dto.getTipoRuta() : TipoRuta.RP)
                .enabled(dto.isEnabled())
                .empleadosActuales(new HashSet<>())
                .build();
    }

    public static RutasResponseDTO RutasEntityToResponse(RutasEntity dto){
        return  RutasResponseDTO.builder()
                .id(dto.getId())
                .nombre(dto.getNombre())
                .numero(dto.getNumero())
                .tipoRuta(dto.getTipoRuta() != null ? dto.getTipoRuta() : TipoRuta.RP)
                .notaAsignada(dto.getLetraPredeterminada())
                .vehiculosBase(dto.getVehiculo() != null ? VehiculosValueMapper.VehiculosEntityToResponse(dto.getVehiculo()) : null )
                .choferBase(dto.getChofer() != null ? EmpleadosValueMapper.EmpleadosEntityToBasicResponse(dto.getChofer()): null)
                .productoBase(dto.getProductBase() != null ? ProductoComercialValueMapper.ProductoComercialEntityToResponse(dto.getProductBase()) : null)
                .empleadosAsignados(ObjectUtils.isEmpty(dto.getEmpleadosActuales()) ? null : dto.getEmpleadosActuales().stream().map(EmpleadosValueMapper::EmpleadosEntityToBasicResponse).collect(Collectors.toSet()) )
                .enabled(dto.isEnabled())
                .cratedAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .build();
    }


}
