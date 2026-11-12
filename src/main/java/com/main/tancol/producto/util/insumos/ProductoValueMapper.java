package com.main.tancol.producto.util.insumos;

import com.main.tancol.almacen.entities.UnidadesMedidaEntity;
import com.main.tancol.almacen.util.UnidadesMedidaValueMapper;
import com.main.tancol.producto.SubcategoriaProductoBasico;
import com.main.tancol.producto.dto.request.insumo.ProductoRequestDTO;
import com.main.tancol.producto.dto.response.PrecioResponseDTO;
import com.main.tancol.producto.dto.response.insumos.ProductoBasicoSimpleResponseDTO;
import com.main.tancol.producto.dto.response.insumos.ProductoResponseDTO;
import com.main.tancol.producto.entities.PresentacionProductoBasico;
import com.main.tancol.producto.entities.insumos.CategoriaProductoEntity;
import com.main.tancol.producto.entities.insumos.ProductoBasicoEntity;
import com.main.tancol.proveedor.util.ProveedorValueMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;

@Slf4j
public class ProductoValueMapper {


    public static ProductoBasicoEntity ProductoRequestDTOToEntity(ProductoRequestDTO dto, CategoriaProductoEntity categoriaProductoEntity, UnidadesMedidaEntity mDefault) {
        return ProductoBasicoEntity.builder()
                .nombre(dto.getNombre())
                .alias(dto.getAlias())
                .descripcion(dto.getDescripcion())
                .idImagen(dto.getIdImagen())
                .sku(dto.getSku())
                .marca(dto.getMarca())
                .modelo(dto.getModelo())
                .stockMinimo(dto.getStockMinimo())
                .stockMaximo(dto.getStockMaximo())
                .notificarStockMinimo(Boolean.TRUE.equals(dto.getNotificarStockMinimo()))
                .tieneVigencia(Boolean.TRUE.equals(dto.getTieneVigencia()))
                .fechaVigencia(Boolean.TRUE.equals(dto.getTieneVigencia()) ? dto.getFechaVigencia() : null)
                .categoriaProductoEntity(categoriaProductoEntity)
                .enabled(true)
                .unidadMedidaDefault(mDefault)
                .build();

    }

    public static ProductoResponseDTO ProductEntityToResponseDTO(ProductoBasicoEntity dto){
        CategoriaProductoEntity padre = padre(dto);
        return  ProductoResponseDTO.builder()
                .idProducto(dto.getId())
                .nombre(dto.getNombre())
                .alias(dto.getAlias())
                .sku(dto.getSku())
                .descripcion(dto.getDescripcion())
                .idImagen(dto.getIdImagen())
                .enabled(dto.isEnabled())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .nombreCategoria((dto.getCategoriaProductoEntity() == null) ? "Sin categoria" : dto.getCategoriaProductoEntity().getNombre())
                .idCategoriaPadre(padre == null ? null : padre.getId())
                .nombreCategoriaPadre(padre == null ? null : padre.getNombre())
                .unidadMedidaDefault(ObjectUtils.isNotEmpty(dto.getUnidadMedidaDefault()) ? UnidadesMedidaValueMapper.UnidadesMedidaEntityToResponse(dto.getUnidadMedidaDefault()) : null)
                .marca(dto.getMarca())
                .modelo(dto.getModelo())
                .stockMinimo(dto.getStockMinimo())
                .stockMaximo(dto.getStockMaximo())
                .notificarStockMinimo(dto.isNotificarStockMinimo())
                .tieneVigencia(dto.isTieneVigencia())
                .fechaVigencia(dto.getFechaVigencia())
                .build();
    }

    public static ProductoBasicoSimpleResponseDTO ProductEntityToSimpleResponse(ProductoBasicoEntity dto){
        return  ProductoBasicoSimpleResponseDTO.builder()
                .idProducto(dto.getId())
                .nombre(dto.getNombre())
                .alias(dto.getAlias())
                .sku(dto.getSku())
                .descripcion(dto.getDescripcion())
                .idImagen(dto.getIdImagen())
                .categoria(ObjectUtils.isNotEmpty(dto.getCategoriaProductoEntity()) ? CategoriaProductoBasicoValueMapper.EntityToCategoriaProductoSimpleResponseDTO(dto.getCategoriaProductoEntity()) : null)
                .enabled(dto.isEnabled())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .marca(dto.getMarca())
                .modelo(dto.getModelo())
                .stockMinimo(dto.getStockMinimo())
                .stockMaximo(dto.getStockMaximo())
                .notificarStockMinimo(dto.isNotificarStockMinimo())
                .tieneVigencia(dto.isTieneVigencia())
                .fechaVigencia(dto.getFechaVigencia())
                .build();
    }


    private static CategoriaProductoEntity padre(ProductoBasicoEntity producto) {
        if (!SubcategoriaProductoBasico.habilitada() || producto.getCategoriaProductoEntity() == null) {
            return null;
        }
        return producto.getCategoriaProductoEntity().getCategoriaPadre();
    }

    public static PrecioResponseDTO PrecioEntityToResponse(PresentacionProductoBasico dto){
        return PrecioResponseDTO.builder()
                .idPrecio(dto.getId())
                .productoEntity(ProductoValueMapper.ProductEntityToResponseDTO(dto.getProductoBasicoEntity()))
                .proveedorEntity(ProveedorValueMapper.ProveedorEntityToResponse(dto.getProveedorEntity()))
                .precio(dto.getPrecio())
                .createdAt(dto.getCreatedAt())
                .build();

    }


}
