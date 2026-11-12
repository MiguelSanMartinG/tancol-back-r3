package com.main.tancol.producto.service.interfaces;

import com.main.tancol.producto.dto.request.ProductoRequestDTO;
import com.main.tancol.producto.dto.response.ProductoResponseDTO;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface IProductoService {
    ProductoResponseDTO create(ProductoRequestDTO dto);

    List<ProductoResponseDTO> getAll(Map<String, String> params);

    ProductoResponseDTO getById(UUID id);

    ProductoResponseDTO update(UUID id ,ProductoRequestDTO dto);

    ProductoResponseDTO delete(UUID dto);
}

