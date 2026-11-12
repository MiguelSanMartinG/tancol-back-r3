package com.main.tancol.almacen.util;

import com.main.tancol.producto.dto.request.ProductoRequestDTO;
import com.main.tancol.almacen.dto.response.AlmacenResponseDTO;
import com.main.tancol.producto.dto.request.CategoriaRequestDTO;
import com.main.tancol.producto.dto.response.CategoriaResponseDTO;
import com.main.tancol.producto.dto.response.ProductoResponseDTO;
import com.main.tancol.almacen.entities.Almacen;
import com.main.tancol.producto.entities.CategoriaProductoEntity;
import com.main.tancol.producto.entities.ProductoEntity;

public class ValueMapper {

    public static AlmacenResponseDTO EntityToAlmacenResponseDTO(Almacen dto){
        return AlmacenResponseDTO.builder()
                .id(dto.getId())
                .nombre(dto.getNombre())
                .status(dto.isEnabled())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .build();
    }


    public static ProductoResponseDTO EntityToProductoResponseDTO(ProductoEntity dto){
        return  ProductoResponseDTO.builder()
                .id(dto.getId())
                .nombre(dto.getNombre())
                .alias(dto.getAlias())
                .sku(dto.getSku())
                .build();
    }

    public static ProductoEntity ProductRequestDtoToEntity(ProductoRequestDTO dto){
        return ProductoEntity.builder()
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .alias(dto.getAlias())
                .sku(dto.getSku())
                .build();
    }

    public static CategoriaProductoEntity CategoriaProductoRequestToEntity(CategoriaRequestDTO dto){
        return CategoriaProductoEntity.builder().nombre(dto.getNombre()).build();

    }

    public static CategoriaResponseDTO EntityToCategoriaProductoResponseDTO(CategoriaProductoEntity dto){


        return CategoriaResponseDTO.builder()
                .id(dto.getId())
                .nombre(dto.getNombre())
                .enabled(dto.isEnabled())
                .updatedAt(dto.getUpdatedAt())
                .createdAt(dto.getCreatedAt())
                .productoList(dto.getProductoEntities() == null ? null :
                        dto.getProductoEntities().stream().map(ValueMapper::EntityToProductoResponseDTO).toList())
                .build();

    }

}
