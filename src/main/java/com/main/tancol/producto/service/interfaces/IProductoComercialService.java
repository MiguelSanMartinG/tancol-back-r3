package com.main.tancol.producto.service.interfaces;

import com.main.tancol.common.dto.PageResponse;
import com.main.tancol.producto.dto.request.comercial.ProductoComercialRequestDTO;
import com.main.tancol.producto.dto.response.comercial.ProductoComercialResponseDTO;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface IProductoComercialService {

    ProductoComercialResponseDTO create(ProductoComercialRequestDTO dto);

    List<ProductoComercialResponseDTO> getAll(Map<String, String> params);

    PageResponse<ProductoComercialResponseDTO> getPage(Map<String, String> params);

    ProductoComercialResponseDTO getById(UUID id);

    ProductoComercialResponseDTO update(UUID id ,ProductoComercialRequestDTO dto);

    ProductoComercialResponseDTO delete(UUID dto);
}
