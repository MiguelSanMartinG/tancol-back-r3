package com.main.tancol.medidores.util;


import com.main.tancol.empleados.entities.DepartamentoEntity;
import com.main.tancol.empleados.util.DepartamentoMapper;
import com.main.tancol.medidores.dto.request.SistemasDeAlmacenamientoRequestDTO;
import com.main.tancol.medidores.dto.response.SistemasDeAlmacenamientoResponseDTO;
import com.main.tancol.medidores.entities.SistemasDeAlmacenamientoEntity;

public class SistemasDeAlmacenamientoValueMapper {

        public static SistemasDeAlmacenamientoEntity SistemasDeAlmacenamientoRequestToEntity(SistemasDeAlmacenamientoRequestDTO dto, DepartamentoEntity departamento) {
        return SistemasDeAlmacenamientoEntity.builder()
                .nombre(dto.getNombre())
                .alias(dto.getAlias())
                .capacidad(dto.getCapacidad())
                .build();

    }

    public static SistemasDeAlmacenamientoResponseDTO SistemasDeAlmacenamientoEntityToResponse(SistemasDeAlmacenamientoEntity dto){
        return  SistemasDeAlmacenamientoResponseDTO.builder()
                .id(dto.getId())
                .nombre(dto.getNombre())
                .alias(dto.getAlias())
                .departamento(DepartamentoMapper.DepartamentoEntityToResponseDTO(dto.getDepartamento()))
                .capacidad(dto.getCapacidad())
                .enabled(dto.isEnabled())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .build();
    }


}
