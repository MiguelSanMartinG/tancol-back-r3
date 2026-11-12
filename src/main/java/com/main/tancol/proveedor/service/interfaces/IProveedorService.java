package com.main.tancol.proveedor.service.interfaces;

import com.main.tancol.proveedor.dto.request.ProveedorRequestDTO;
import com.main.tancol.proveedor.dto.response.ProveedorResponseDTO;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface IProveedorService {
    ProveedorResponseDTO create(ProveedorRequestDTO dto);

    List<ProveedorResponseDTO> getAll(Map<String, String> params);

    ProveedorResponseDTO getById(UUID id);

    ProveedorResponseDTO update(UUID id ,ProveedorRequestDTO dto);

    ProveedorResponseDTO delete(UUID dto);
}
