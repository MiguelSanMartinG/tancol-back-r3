package com.main.tancol.proveedor.service.interfaces;

import com.main.tancol.proveedor.dto.request.TipoProveedorRequestDTO;
import com.main.tancol.proveedor.dto.response.TipoProveedorResponseDTO;

import java.util.*;

public interface ITipoProveedorService {

    TipoProveedorResponseDTO create(TipoProveedorRequestDTO dto);

    List<TipoProveedorResponseDTO> getAll(Map<String, String> params);

    TipoProveedorResponseDTO getById(UUID id);

    TipoProveedorResponseDTO update(UUID id ,TipoProveedorRequestDTO dto);

    TipoProveedorResponseDTO delete(UUID dto);
}
