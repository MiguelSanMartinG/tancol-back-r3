package com.main.tancol.logistica.util;


import com.main.tancol.logistica.dto.request.ProductosFueraEspecificacionRequestDTO;
import com.main.tancol.logistica.dto.response.ProductosFueraEspecificacionResponseDTO;
import com.main.tancol.logistica.entities.bitacora.comisiones.ProductosFueraEspecificacionEntity;
import com.main.tancol.producto.util.ConceptosValueMapper;
import org.apache.commons.lang3.ObjectUtils;

public class ProductosFueraEspecificacionValueMapper {

        public static ProductosFueraEspecificacionEntity ProductosFueraEspecificacionRequestToEntity(ProductosFueraEspecificacionRequestDTO dto) {
        return ProductosFueraEspecificacionEntity.builder()
                .build();

    }

    public static ProductosFueraEspecificacionResponseDTO ProductosFueraEspecificacionEntityToResponse(ProductosFueraEspecificacionEntity dto){
            if(dto == null) return null;
        return  ProductosFueraEspecificacionResponseDTO.builder()
                .idPFE(dto.getIdPFE())
                .cantidad(dto.getCantidad())
                .concepto(ObjectUtils.isNotEmpty(dto.getConcepto()) ? ConceptosValueMapper.ConceptosEntityToResponse(dto.getConcepto()) : null)
//                .createdAt(dto.getCreatedAt())
//                .updatedAt(dto.getUpdatedAt())
                .build();
    }


}
