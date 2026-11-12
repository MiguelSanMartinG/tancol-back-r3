package com.main.tancol.cliente.util;


import com.main.tancol.cliente.dto.request.TiposClientesRequestDTO;
import com.main.tancol.cliente.dto.response.TipoClienteSimpleResponseDTO;
import com.main.tancol.cliente.dto.response.TiposClientesResponseDTO;
import com.main.tancol.cliente.entities.TipoClienteEntity;

import java.util.ArrayList;

public class TiposClientesValueMapper {

        public static TipoClienteEntity TiposClientesRequestToEntity(TiposClientesRequestDTO dto) {
        return TipoClienteEntity.builder()
                .nombre(dto.getNombre())
                .alias(dto.getAlias())
                .clientes(new ArrayList<>())
                .build();

    }

    public static TiposClientesResponseDTO TiposClientesEntityToResponse(TipoClienteEntity dto){
        return  TiposClientesResponseDTO.builder()
                .idTiposClientes(dto.getId())
                .nombre(dto.getNombre())
                .alias(dto.getAlias())
                .clientes(dto.getClientes().stream().map(ClienteValueMapper::ClienteEntityToResponse).toList())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .enabled(dto.isEnabled())
                .build();
    }

    public static TipoClienteSimpleResponseDTO TiposClientesEntityToSimpleResponse(TipoClienteEntity dto){
        return  TipoClienteSimpleResponseDTO.builder()
                .idTipoCliente(dto.getId())
                .nombre(dto.getNombre())
                .alias(dto.getAlias())
                .enabled(dto.isEnabled())
                .build();
    }


}
