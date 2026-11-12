package com.main.tancol.logistica.util;

import com.main.tancol.almacen.entities.UnidadesMedidaEntity;
import com.main.tancol.almacen.util.UnidadesMedidaValueMapper;
import com.main.tancol.logistica.dto.request.VehiculosRequestDTO;
import com.main.tancol.logistica.dto.response.VehiculosBasicResponseDTO;
import com.main.tancol.logistica.dto.response.VehiculosResponseDTO;
import com.main.tancol.logistica.entities.rutas.VehiculosEntity;
import org.apache.commons.lang3.ObjectUtils;

public class VehiculosValueMapper {

    public static VehiculosEntity VehiculosRequestToEntity(VehiculosRequestDTO dto, UnidadesMedidaEntity unidadesMedida) {
        return VehiculosEntity.builder()
                .nombre(dto.getNombre())
                .placas(dto.getPlacas())
                .nombre(dto.getNombre())
                .capacidadMaxima(dto.getCapacidadMaxima())
                .unidadesMedidaEntity(unidadesMedida)
                .documentoPoliza(dto.getDocumentoPoliza())
                .numeroPoliza(dto.getNumeroPoliza())
                .vigenciaPoliza(dto.getVigenciaPoliza())
                .estatusVehiculo(dto.getStatusVehiculo())
                .descripcion(dto.getDescripcion())
                .build();

    }

    public static VehiculosResponseDTO VehiculosEntityToResponse(VehiculosEntity dto) {
        return VehiculosResponseDTO.builder()
                .id(dto.getId())
                .nombre(dto.getNombre())
                .placas(dto.getPlacas())
                .nombre(dto.getNombre())
                .capacidadMaxima(dto.getCapacidadMaxima())
                .unidadMedida(ObjectUtils.isNotEmpty(dto.getUnidadesMedidaEntity()) ? UnidadesMedidaValueMapper.UnidadesMedidaEntityToResponse(dto.getUnidadesMedidaEntity()): null)
                .numeroPoliza(dto.getNumeroPoliza())
                .vigenciaPoliza(dto.getVigenciaPoliza())
                .status(dto.getEstatusVehiculo())
                .descripcion(dto.getDescripcion())
                .cratedAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .enabled(dto.isEnabled())
                .documentoPoliza(dto.getDocumentoPoliza())
                .build();
    }

    public static VehiculosBasicResponseDTO VehiculosEntityToBasicResponse(VehiculosEntity dto) {
        return VehiculosBasicResponseDTO.builder()
                .id(dto.getId())
                .nombre(dto.getNombre())
                .placas(dto.getPlacas())
                .nombre(dto.getNombre())
                .capacidadMaxima(dto.getCapacidadMaxima())
                .unidadMedida(UnidadesMedidaValueMapper.UnidadesMedidaEntityToResponse(dto.getUnidadesMedidaEntity()))
                .enabled(dto.isEnabled())
                .build();
    }

}
