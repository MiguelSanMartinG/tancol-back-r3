package com.main.tancol.almacen.service.interfaces;

import com.main.tancol.producto.dto.request.insumo.CategoriaRequestDTO;
import com.main.tancol.producto.dto.response.insumos.CategoriaExistenciaResponseDTO;
import com.main.tancol.producto.dto.response.insumos.CategoriaProductosResponseDTO;
import com.main.tancol.producto.dto.response.insumos.CategoriaRecuentoResponseDTO;
import com.main.tancol.producto.dto.response.insumos.CategoriaResponseDTO;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface ICategoriaProductoService {
    CategoriaResponseDTO createCategoria(CategoriaRequestDTO dto);

    CategoriaResponseDTO readByID(UUID id);

    CategoriaResponseDTO updateCategoria(UUID id, CategoriaRequestDTO dto);

    CategoriaResponseDTO deleteCategoria(UUID id);

    List<CategoriaResponseDTO> getAll(Map<String, String> params);

    CategoriaProductosResponseDTO productos(UUID id);

    CategoriaRecuentoResponseDTO recuento(UUID id);

    CategoriaExistenciaResponseDTO existencia(UUID id);

    Set<UUID> expandirConSubcategorias(Collection<UUID> ids);
}
