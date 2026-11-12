package com.main.tancol.liquidacion.util.conceptos;


import com.main.tancol.liquidacion.dto.request.ConceptosVentasRequestDTO;
import com.main.tancol.liquidacion.dto.response.ConceptosVentasResponseDTO;
import com.main.tancol.liquidacion.entities.ConceptosVentasEntity;

public class ConceptosVentasValueMapper {

        public static ConceptosVentasEntity ConceptosRequestToEntity(ConceptosVentasRequestDTO dto) {
        return ConceptosVentasEntity.builder()
                .name(dto.getConcepto())
                .isExclude(dto.isExclude())
                .descuento(dto.isDescuento())
                .concepto(dto.getConcepto())
                .x(dto.isX())
                .cancelado(dto.isCancelado())
                .build();

    }

    public static ConceptosVentasResponseDTO ConceptosEntityToResponse(ConceptosVentasEntity dto){
            if(dto == null)return null;
        return  ConceptosVentasResponseDTO.builder()
                .id(dto.getId())
                .name(dto.getName())
                .concepto(dto.getConcepto())
                .exclude(dto.isExclude())
                .cancelado(dto.isCancelado())
                .descuento(dto.isDescuento())
                .enabled(dto.isEnabled())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .build();
    }


}
