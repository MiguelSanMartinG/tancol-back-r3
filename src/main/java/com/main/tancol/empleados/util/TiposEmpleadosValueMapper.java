package com.main.tancol.empleados.util;


import com.main.tancol.empleados.dto.request.*;
import com.main.tancol.empleados.dto.response.*;
import com.main.tancol.empleados.entities.*;

public class TiposEmpleadosValueMapper {

        public static TiposEmpleadosEntity TiposEmpleadosRequestToEntity(TiposEmpleadosRequestDTO dto) {
        return TiposEmpleadosEntity.builder()
                .nombre(dto.getNombre())
                .alias(dto.getAlias())
                .enabled(dto.isEnabled())
                .build();

    }

    public static TiposEmpleadosResponseDTO TiposEmpleadosEntityToResponse(TiposEmpleadosEntity dto){
        return  TiposEmpleadosResponseDTO.builder()
                .idTipoEmpleado(dto.getId())
                .nombre(dto.getNombre())
                .alias(dto.getAlias())
                .empleados(dto.getEmpleadosEntities() != null ? dto.getEmpleadosEntities().stream().map(EmpleadosValueMapper::EmpleadosEntityToResponse).toList() : null)
                .updatedAt(dto.getUpdatedAt())
                .createdAt(dto.getCreatedAt())
                .enabled(dto.isEnabled())
                .build();
    }

    public static TiposEmpleadosSimpleResponseDTO TiposEmpleadosEntityToResponseSimpleDTO(TiposEmpleadosEntity dto){
        return  TiposEmpleadosSimpleResponseDTO.builder()
                .idTipoEmpleado(dto.getId())
                .nombre(dto.getNombre())
                .alias(dto.getAlias())
                .updatedAt(dto.getUpdatedAt())
                .createdAt(dto.getCreatedAt())
                .enabled(dto.isEnabled())
                .build();
    }




}
