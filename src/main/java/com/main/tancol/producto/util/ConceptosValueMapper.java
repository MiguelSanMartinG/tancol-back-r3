package com.main.tancol.producto.util;


import com.main.tancol.producto.dto.request.ConceptosRequestDTO;
import com.main.tancol.producto.dto.response.ConceptosResponseDTO;
import com.main.tancol.producto.entities.ConceptosEntity;

public class ConceptosValueMapper {

        public static ConceptosEntity ConceptosRequestToEntity(ConceptosRequestDTO dto) {
        return ConceptosEntity.builder()
                .tipoRezago(dto.getTipoRezago())
                .motivo(dto.getMotivo())
                .build();

    }

    public static ConceptosResponseDTO ConceptosEntityToResponse(ConceptosEntity dto){
        return  ConceptosResponseDTO.builder()
                .id(dto.getId())
                .tipoRezago(dto.getTipoRezago())
                .motivo(dto.getMotivo())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .enabled(dto.isEnabled())
                .build();
    }


}
