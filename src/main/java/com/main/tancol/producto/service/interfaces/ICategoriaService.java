package com.main.tancol.producto.service.interfaces;

import com.main.tancol.producto.dto.request.insumo.CategoriaRequestDTO;
import com.main.tancol.producto.dto.response.insumos.CategoriaResponseDTO;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ICategoriaService {

    CategoriaResponseDTO crearCategoria(CategoriaRequestDTO dto);

    List<CategoriaResponseDTO> getAll(Map<String, String> params);

    CategoriaResponseDTO getById(UUID id);

    CategoriaResponseDTO updateCategoria(CategoriaRequestDTO dto);

    CategoriaResponseDTO deleteCategoria(UUID dto);

}
