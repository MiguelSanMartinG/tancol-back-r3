package com.main.tancol.producto.service.interfaces;

import com.main.tancol.producto.dto.request.comercial.CategoriaProductoComercialRequestDTO;
import com.main.tancol.producto.dto.response.comercial.CategoriaProductoComercialResponseDTO;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ICategoriaProductoComercialService {

    CategoriaProductoComercialResponseDTO create(CategoriaProductoComercialRequestDTO dto);

    List<CategoriaProductoComercialResponseDTO> getAll(Map<String, String> params);

    CategoriaProductoComercialResponseDTO getById(UUID id);

    CategoriaProductoComercialResponseDTO update(UUID id ,CategoriaProductoComercialRequestDTO dto);

    CategoriaProductoComercialResponseDTO delete(UUID dto);
}
