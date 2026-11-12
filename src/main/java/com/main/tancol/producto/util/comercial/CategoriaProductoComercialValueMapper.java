package com.main.tancol.producto.util.comercial;


import com.main.tancol.producto.dto.request.comercial.CategoriaProductoComercialRequestDTO;
import com.main.tancol.producto.dto.response.comercial.CategoriaProductoComercialResponseDTO;
import com.main.tancol.producto.dto.response.comercial.CategoriaProductoComercialSimpleResponseDTO;
import com.main.tancol.producto.entities.comerciales.CategoriaProductoComercialEntity;

import java.util.ArrayList;

public class CategoriaProductoComercialValueMapper {

    public static CategoriaProductoComercialEntity CategoriaProductoComercialRequestToEntity(CategoriaProductoComercialRequestDTO dto) {
        return CategoriaProductoComercialEntity.builder()
                .nombre(dto.getNombre())
                .productosComerciales(new ArrayList<>())
                .build();

    }

    public static CategoriaProductoComercialResponseDTO CategoriaProductoComercialEntityToResponse(CategoriaProductoComercialEntity dto) {
        return CategoriaProductoComercialResponseDTO.builder()
                .idCategoriaComercial(dto.getId())
                .nombre(dto.getNombre())
                .productoList(null)
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .enabled(dto.isEnabled())
                .build();
    }

    public static CategoriaProductoComercialSimpleResponseDTO CategoriaProductoComercialEntityToSimpleResponse(CategoriaProductoComercialEntity dto) {
        return CategoriaProductoComercialSimpleResponseDTO.builder()
                .idCategoriaComercial(dto.getId())
                .nombre(dto.getNombre())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .enabled(dto.isEnabled())
                .build();
    }
}



