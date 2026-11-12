package com.main.tancol.producto.service.interfaces;

import com.main.tancol.producto.dto.request.PrecioRequestDTO;
import com.main.tancol.producto.dto.response.PrecioResponseDTO;

import java.util.List;
import java.util.UUID;

public interface IPrecioService {
    PrecioResponseDTO getPrecio(UUID proveedorId, UUID productoId);
    PrecioResponseDTO setPrecio(PrecioRequestDTO requestDTO);
    List<PrecioResponseDTO> findAll();
}
