package com.main.tancol.proveedor.util;


import com.main.tancol.proveedor.dto.request.ProveedorRequestDTO;
import com.main.tancol.proveedor.dto.request.TipoImpuestoRequestDTO;
import com.main.tancol.proveedor.dto.response.ProveedorResponseDTO;
import com.main.tancol.proveedor.dto.response.TipoImpuestoResponseDTO;
import com.main.tancol.proveedor.entity.ProveedorEntity;
import com.main.tancol.proveedor.entity.TipoImpuesto;

public class ValueMapper {


    public static ProveedorEntity ProveedorRequestToEntity(ProveedorRequestDTO dto) {
        return ProveedorEntity.builder()
                .nombre(dto.getNombre())
                .razonSocial(dto.getRazonSocial())
                .enabled(dto.isEnabled())
                .build();
//dto.getProductos() == null ? null :
//                        dto.getProductos().stream().map(ValueMapper::EntityToProductoResponseDTO).toList()
    }

    public static ProveedorResponseDTO ProveedorEntityToResponse(ProveedorEntity dto){
        return  ProveedorResponseDTO.builder()
                .id(dto.getId())
                .nombre(dto.getNombre())
                .razonSocial(dto.getRazonSocial())
                .enabled(dto.isEnabled())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .build();
    }

    public static TipoImpuesto TipoImpuestoRequestToEntity(TipoImpuestoRequestDTO dto){
        return TipoImpuesto.builder()
                .nombre(dto.getNombre())
                .porcentaje(dto.getPorcentaje())
                .build();
    }

    public static TipoImpuestoResponseDTO TipoImpuestoEntityToResponse(TipoImpuesto dto){
        return TipoImpuestoResponseDTO.builder()
                .id(dto.getId())
                .porcentaje(dto.getPorcentaje())
                .nombre(dto.getNombre())
                .enabled(dto.isEnabled())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .build();
    }
    }



