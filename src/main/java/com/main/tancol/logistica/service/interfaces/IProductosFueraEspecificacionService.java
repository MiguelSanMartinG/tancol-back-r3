package com.main.tancol.logistica.service.interfaces;

import com.main.tancol.logistica.dto.request.ProductosFueraEspecificacionRequestDTO;
import com.main.tancol.logistica.dto.response.ProductosFueraEspecificacionResponseDTO;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface IProductosFueraEspecificacionService {

    ProductosFueraEspecificacionResponseDTO create(ProductosFueraEspecificacionRequestDTO dto);

    List<ProductosFueraEspecificacionResponseDTO> getAll(Map<String, String> params);

    ProductosFueraEspecificacionResponseDTO getById(UUID id);

    ProductosFueraEspecificacionResponseDTO update(UUID id ,ProductosFueraEspecificacionRequestDTO dto);

    ProductosFueraEspecificacionResponseDTO delete(UUID dto);
}
