package com.main.tancol.permisos.util;


import com.main.tancol.permisos.dto.request.ModuleRequestDTO;
import com.main.tancol.permisos.dto.response.ModuleResponseDTO;
import com.main.tancol.permisos.entities.ModuleEntity;
import org.apache.commons.lang3.ObjectUtils;

public class ModuleValueMapper {

        public static ModuleEntity ModuleRequestToEntity(ModuleRequestDTO dto) {
        return ModuleEntity.builder()
                .name(dto.getName())
                .rutaModulo(dto.getRutaModulo())
                .build();

    }

    public static ModuleResponseDTO ModuleEntityToResponse(ModuleEntity dto){
        return  ModuleResponseDTO.builder()
                .idModulo(dto.getId())
                .name(dto.getName())
                .ruta(dto.getRutaModulo())
                .parent(ObjectUtils.isEmpty(dto.getParent())? null: ModuleEntityToResponse(dto.getParent()))
                .build();
    }


}
