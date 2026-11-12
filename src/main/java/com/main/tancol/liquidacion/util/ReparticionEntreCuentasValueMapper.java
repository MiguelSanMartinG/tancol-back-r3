package com.main.tancol.liquidacion.util;


import com.main.tancol.liquidacion.dto.request.ReparticionEntreCuentasRequestDTO;
import com.main.tancol.liquidacion.dto.response.ReparticionEntreCuentasResponseDTO;
import com.main.tancol.liquidacion.entities.ReparticionEntreCuentasEntity;

public class ReparticionEntreCuentasValueMapper {

        public static ReparticionEntreCuentasEntity ReparticionEntreCuentasRequestToEntity(ReparticionEntreCuentasRequestDTO dto) {
        return ReparticionEntreCuentasEntity.builder()

                .build();

    }

    public static ReparticionEntreCuentasResponseDTO ReparticionEntreCuentasEntityToResponse(ReparticionEntreCuentasEntity dto){
        return  ReparticionEntreCuentasResponseDTO.builder()
                .cuentaA(dto.getCuentaA())
                .cuentaB(dto.getCuentaB())
                .cuentaC(dto.getCuentaC())
                .usuarioQueEdito(dto.getUsuarioQueEdito())
                .id(dto.getId())
                .createdAt(dto.getCreatedAt())
                .build();
    }


}
