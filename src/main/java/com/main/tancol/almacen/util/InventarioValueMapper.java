package com.main.tancol.almacen.util;


import com.main.tancol.almacen.dto.request.*;
import com.main.tancol.almacen.dto.response.InventarioResponseDTO;
import com.main.tancol.almacen.entities.AlmacenEntity;
import com.main.tancol.almacen.entities.InventarioEntity;
import com.main.tancol.almacen.entities.UnidadesMedidaEntity;
import com.main.tancol.producto.entities.insumos.ProductoBasicoEntity;
import com.main.tancol.producto.util.insumos.ProductoValueMapper;

public class InventarioValueMapper {

        public static InventarioEntity InventarioRequestToEntity(InventarioRequestDTO dto, ProductoBasicoEntity productoBasico, AlmacenEntity almacen, UnidadesMedidaEntity unidadesMedidaEntity) {
        return InventarioEntity.builder()
                .almacen(almacen)
                .productoBasicoEntity(productoBasico)
                .cantidad(dto.getCantidad())
                .unidadDeMedida(unidadesMedidaEntity)
                .build();

    }

    public static InventarioResponseDTO InventarioEntityToResponse(InventarioEntity dto){
        return  InventarioResponseDTO.builder()
                .idInventario(dto.getId())
                .producto(ProductoValueMapper.ProductEntityToSimpleResponse(dto.getProductoBasicoEntity()))
                .unidadesMedida(dto.getUnidadDeMedida() != null ? UnidadesMedidaValueMapper.UnidadesMedidaEntityToResponse(dto.getUnidadDeMedida()): null)
                .cantidad(dto.getCantidad())
                .almacen(AlmacenValueMapper.AlmacenEntityToSimpleResponse(dto.getAlmacen()))
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .build();
    }


}
