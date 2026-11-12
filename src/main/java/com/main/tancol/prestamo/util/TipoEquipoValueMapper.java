package com.main.tancol.prestamo.util;

import com.main.tancol.prestamo.dto.request.TipoEquipoRequestDTO;
import com.main.tancol.prestamo.dto.response.TipoEquipoResponseDTO;
import com.main.tancol.prestamo.dto.response.TipoEquipoSimpleResponseDTO;
import com.main.tancol.prestamo.entities.TipoEquipoEntity;

import java.util.ArrayList;

public class TipoEquipoValueMapper {

    public static TipoEquipoEntity requestToEntity(TipoEquipoRequestDTO dto) {
        return TipoEquipoEntity.builder()
                .nombre(dto.getNombre())
                .alias(dto.getAlias())
                .equipos(new ArrayList<>())
                .build();
    }

    public static TipoEquipoResponseDTO entityToResponse(TipoEquipoEntity entity) {
        if (entity == null) {
            return null;
        }
        return TipoEquipoResponseDTO.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .alias(entity.getAlias())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .enabled(entity.isEnabled())
                .build();
    }

    public static TipoEquipoSimpleResponseDTO entityToSimpleResponse(TipoEquipoEntity entity) {
        if (entity == null) {
            return null;
        }
        return TipoEquipoSimpleResponseDTO.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .alias(entity.getAlias())
                .enabled(entity.isEnabled())
                .build();
    }
}
