package com.main.tancol.permisos.util;


import com.main.tancol.permisos.dto.request.RoleRequestDTO;
import com.main.tancol.permisos.dto.response.RoleResponseDTO;
import com.main.tancol.permisos.entities.RoleEntity;

public class RoleValueMapper {

        public static RoleEntity RoleRequestToEntity(RoleRequestDTO dto) {
        return RoleEntity.builder()
                .build();

    }

    public static RoleResponseDTO RoleEntityToResponse(RoleEntity dto){
        return  RoleResponseDTO.builder()
                .id(dto.getId())
                .name(dto.getName())
                .build();
    }


}
