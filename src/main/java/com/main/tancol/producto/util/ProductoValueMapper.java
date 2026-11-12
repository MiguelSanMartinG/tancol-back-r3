package com.main.tancol.producto.util;

import com.main.tancol.producto.dto.request.ProductoRequestDTO;
import com.main.tancol.producto.dto.response.ProductoResponseDTO;
import com.main.tancol.producto.dto.response.PrecioResponseDTO;
import com.main.tancol.producto.entities.CategoriaProductoEntity;
import com.main.tancol.producto.entities.ProductoEntity;
import com.main.tancol.producto.entities.PrecioEntity;
import com.main.tancol.proveedor.util.ValueMapper;

public class ProductoValueMapper {


    public static ProductoEntity ProductoRequestDTOToEntity(ProductoRequestDTO dto, CategoriaProductoEntity categoriaProductoEntity) {
        return ProductoEntity.builder()
                .nombre(dto.getNombre())
                .alias(dto.getAlias())
                .descripcion(dto.getDescripcion())
                .sku(dto.getSku())
                .categoriaProductoEntity(categoriaProductoEntity)
                .enabled(true)
                .build();

    }

    public static ProductoResponseDTO ProductEntityToResponseDTO(ProductoEntity dto){
        return  ProductoResponseDTO.builder()
                .id(dto.getId())
                .nombre(dto.getNombre())
                .alias(dto.getAlias())
                .sku(dto.getSku())
                .descripcion(dto.getDescripcion())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .nombreCategoria((dto.getCategoriaProductoEntity() != null) ? dto.getCategoriaProductoEntity().getNombre() : "Sin categoria")
                .build();
    }


    public static PrecioResponseDTO PrecioEntityToResponse(PrecioEntity dto){
        return PrecioResponseDTO.builder()
                .idPrecio(dto.getId())
                .productoEntity(ProductoValueMapper.ProductEntityToResponseDTO(dto.getProductoEntity()))
                .proveedorEntity(ValueMapper.ProveedorEntityToResponse(dto.getProveedorEntity()))
                .precio(dto.getPrecio())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .build();

    }


}
