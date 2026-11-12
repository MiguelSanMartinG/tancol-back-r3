package com.main.tancol.producto.util.insumos;

import com.main.tancol.producto.SubcategoriaProductoBasico;
import com.main.tancol.producto.dto.response.insumos.CategoriaProductoSimpleResponseDTO;
import com.main.tancol.producto.entities.insumos.CategoriaProductoEntity;

public class CategoriaProductoBasicoValueMapper {

    public static CategoriaProductoSimpleResponseDTO EntityToCategoriaProductoSimpleResponseDTO(CategoriaProductoEntity dto){

        CategoriaProductoEntity padre = SubcategoriaProductoBasico.habilitada() ? dto.getCategoriaPadre() : null;
        return CategoriaProductoSimpleResponseDTO.builder()
                .idCategoria(dto.getId())
                .nombre(dto.getNombre())
                .enabled(dto.isEnabled())
                .idCategoriaPadre(padre == null ? null : padre.getId())
                .nombreCategoriaPadre(padre == null ? null : padre.getNombre())
                .build();
    }
}
