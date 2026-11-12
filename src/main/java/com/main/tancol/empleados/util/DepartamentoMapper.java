package com.main.tancol.empleados.util;

import com.main.tancol.almacen.dto.response.AlmacenResponseDTO;
import com.main.tancol.almacen.entities.AlmacenEntity;
import com.main.tancol.empleados.dto.request.DepartamentoRequestDTO;
import com.main.tancol.empleados.dto.response.DepartamentoResponseDTO;
import com.main.tancol.empleados.dto.response.DepartamentoSimpleResponseDTO;
import com.main.tancol.empleados.entities.DepartamentoEntity;
import com.main.tancol.producto.SubcategoriaProductoBasico;
import com.main.tancol.producto.dto.response.insumos.CategoriaHijaResponseDTO;
import com.main.tancol.producto.dto.response.insumos.CategoriaResponseDTO;
import com.main.tancol.producto.entities.insumos.CategoriaProductoEntity;
import com.main.tancol.producto.util.insumos.ProductoValueMapper;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Comparator;
import java.util.List;

public class DepartamentoMapper {

    public static AlmacenResponseDTO EntityToAlmacenResponseDTO(AlmacenEntity dto){
        return AlmacenResponseDTO.builder()
                .id(dto.getId())
                .nombre(dto.getNombre())
                .status(dto.isEnabled())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .build();
    }

    public static CategoriaResponseDTO EntityToCategoriaProductoResponseDTO(CategoriaProductoEntity dto){

        boolean jerarquia = SubcategoriaProductoBasico.habilitada();
        CategoriaProductoEntity padre = jerarquia ? dto.getCategoriaPadre() : null;
        List<CategoriaHijaResponseDTO> subcategorias = !jerarquia || dto.getSubcategorias() == null
                ? List.of()
                : dto.getSubcategorias().stream()
                .filter(hija -> !hija.isDeleted())
                .sorted(Comparator.comparing(CategoriaProductoEntity::getNombre, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .map(hija -> CategoriaHijaResponseDTO.builder()
                        .idCategoria(hija.getId())
                        .nombre(hija.getNombre())
                        .build())
                .toList();

        return CategoriaResponseDTO.builder()
                .idCategoria(dto.getId())
                .nombre(dto.getNombre())
                .enabled(dto.isEnabled())
                .updatedAt(dto.getUpdatedAt())
                .createdAt(dto.getCreatedAt())
                .idCategoriaPadre(padre == null ? null : padre.getId())
                .nombreCategoriaPadre(padre == null ? null : padre.getNombre())
                .subcategorias(subcategorias)
                .productoList(dto.getProductoEntities() == null ? null : dto.getProductoEntities().stream().map(ProductoValueMapper::ProductEntityToResponseDTO).toList())
                .build();
    }

    public static  DepartamentoResponseDTO DepartamentoEntityToResponseDTO(DepartamentoEntity dto){

        if (ObjectUtils.isEmpty(dto)) return null;

        return DepartamentoResponseDTO.builder()
                .idDepartamento(dto.getId())
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .alias(dto.getAlias())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .status(dto.isEnabled())
                .empleados(dto.getEmpleadosEntities() == null ? null :
                        dto.getEmpleadosEntities().stream().map(EmpleadosValueMapper::EmpleadosEntityToResponse).toList())
                .build();
    }

    public static DepartamentoSimpleResponseDTO DepartamentoEntityToResponseSimpleDTO(DepartamentoEntity dto){

        return DepartamentoSimpleResponseDTO.builder()
                .idDepartamento(dto.getId())
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .alias(dto.getAlias())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .enabled(dto.isEnabled())
                .build();
    }


    public static DepartamentoEntity DepartamentoRequesToToEntity(DepartamentoRequestDTO dto){
        return DepartamentoEntity.builder()
                .nombre(dto.getNombre())
                .alias(dto.getAlias())
                .descripcion(dto.getDescripcion())
                .build();
    }

}


