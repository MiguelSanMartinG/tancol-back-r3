package com.main.tancol.producto.service.interfaces;

import com.main.tancol.common.dto.PageResponse;
import com.main.tancol.producto.dto.request.insumo.ProductoRequestDTO;
import com.main.tancol.producto.dto.response.insumos.ProductoResponseDTO;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface IProductoService {
    ProductoResponseDTO create(ProductoRequestDTO dto);

    List<ProductoResponseDTO> getAll(Map<String, String> params);

    PageResponse<ProductoResponseDTO> getPage(Map<String, String> params);

    ProductoResponseDTO getById(UUID id);

    ProductoResponseDTO update(UUID id ,ProductoRequestDTO dto);

    ProductoResponseDTO delete(UUID dto);
}

