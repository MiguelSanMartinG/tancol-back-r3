package com.main.tancol.almacen.service.interfaces;

import com.main.tancol.producto.dto.request.CategoriaRequestDTO;
import com.main.tancol.producto.dto.response.CategoriaResponseDTO;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ICategoriaProductoService {
    CategoriaResponseDTO createCategoria(CategoriaRequestDTO dto);

    CategoriaResponseDTO readByID(UUID id);

    CategoriaResponseDTO updateCategoria(UUID id, CategoriaRequestDTO dto);

    CategoriaResponseDTO deleteCategoria(UUID id);

    List<CategoriaResponseDTO> getAll(Map<String, String> params);
}
