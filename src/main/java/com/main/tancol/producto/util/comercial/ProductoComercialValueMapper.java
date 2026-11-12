package com.main.tancol.producto.util.comercial;


import com.main.tancol.almacen.entities.UnidadesMedidaEntity;
import com.main.tancol.almacen.util.UnidadesMedidaValueMapper;
import com.main.tancol.producto.dto.request.comercial.ProductoComercialRequestDTO;
import com.main.tancol.producto.dto.response.comercial.ProductoComercialResponseBasicoDTO;
import com.main.tancol.producto.dto.response.comercial.ProductoComercialResponseDTO;
import com.main.tancol.producto.entities.comerciales.CategoriaProductoComercialEntity;
import com.main.tancol.producto.entities.comerciales.ProductoComercialEntity;
import org.apache.commons.lang3.ObjectUtils;

public class ProductoComercialValueMapper {

        public static ProductoComercialEntity ProductoComercialRequestToEntity(ProductoComercialRequestDTO dto, CategoriaProductoComercialEntity categoria, UnidadesMedidaEntity mDefault) {
        return ProductoComercialEntity.builder()
                .nombre(dto.getNombre())
                .alias(dto.getAlias())
                .categoriaProductoComercialEntity(categoria)
                .precio(dto.getPrecio())
                .sku(dto.getSku())
                .presentacion(dto.getPresentacion())
                .descripcion(dto.getDescripcion())
                .idImagen(dto.getIdImagen())
                .unidadMedidaDefault(mDefault)
                .build();

    }

    public static ProductoComercialResponseDTO ProductoComercialEntityToResponse(ProductoComercialEntity dto){
            if(dto == null) return null;
        return  ProductoComercialResponseDTO.builder()
                .idProductoComercial(dto.getId())
                .nombre(dto.getNombre())
                .alias(dto.getAlias())
                .precio(dto.getPrecio())
                .sku(dto.getSku())
                .presentacion(dto.getPresentacion())
                .descripcion(dto.getDescripcion())
                .idImagen(dto.getIdImagen())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .enabled(dto.isEnabled())
                .categoriaProductoComercial(CategoriaProductoComercialValueMapper.CategoriaProductoComercialEntityToSimpleResponse(dto.getCategoriaProductoComercialEntity()))
                .unidadMedidaDefault(ObjectUtils.isNotEmpty(dto.getUnidadMedidaDefault()) ? UnidadesMedidaValueMapper.UnidadesMedidaEntityToResponse(dto.getUnidadMedidaDefault()) : null)
                .build();
    }

    public static ProductoComercialResponseBasicoDTO ProductoComercialEntityToResponseBasico(ProductoComercialEntity dto){
        return  ProductoComercialResponseBasicoDTO.builder()
                .idProductoComercial(dto.getId())
                .nombre(dto.getNombre())
                .precio(dto.getPrecio())
                .build();
    }


}
