package com.main.tancol.almacen.util;


import com.main.tancol.almacen.dto.request.UnidadesMedidaRequestDTO;
import com.main.tancol.almacen.dto.response.UnidadesMedidaResponseDTO;
import com.main.tancol.almacen.entities.*;
import java.util.List;

public class UnidadesMedidaValueMapper {

        public static UnidadesMedidaEntity UnidadesMedidaRequestToEntity(UnidadesMedidaRequestDTO dto) {
        return UnidadesMedidaEntity.builder()
                .nombre(dto.getNombre())
                .alias(dto.getAlias())
                .build();

    }

    public static UnidadesMedidaResponseDTO UnidadesMedidaEntityToResponse(UnidadesMedidaEntity dto){
        return  UnidadesMedidaResponseDTO.builder()
                .idUnidadDeMedida(dto.getIdUnidadesMedida())
                .nombre(dto.getNombre())
                .alias(dto.getAlias())
                .updatedAt(dto.getUpdatedAt())
                .createdAt(dto.getCreatedAt())
                .build();
    }


}
