package com.main.tancol.producto.service.interfaces;

import com.main.tancol.producto.dto.request.ProductosNoConsignadosRequestDTO;
import com.main.tancol.producto.dto.response.ProductosNoConsignadosResponseDTO;

import java.util.*;

public interface IProductosNoConsignadosService {

    ProductosNoConsignadosResponseDTO create(ProductosNoConsignadosRequestDTO dto);

    List<ProductosNoConsignadosResponseDTO> getAll(Map<String, String> params);

    ProductosNoConsignadosResponseDTO getById(UUID id);

    ProductosNoConsignadosResponseDTO update(UUID id ,ProductosNoConsignadosRequestDTO dto);

    ProductosNoConsignadosResponseDTO delete(UUID dto);
}
