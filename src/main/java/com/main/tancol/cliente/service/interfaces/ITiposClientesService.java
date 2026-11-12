package com.main.tancol.cliente.service.interfaces;

import com.main.tancol.cliente.dto.request.TiposClientesRequestDTO;
import com.main.tancol.cliente.dto.response.TiposClientesResponseDTO;

import java.util.*;

public interface ITiposClientesService {

    TiposClientesResponseDTO create(TiposClientesRequestDTO dto);

    List<TiposClientesResponseDTO> getAll(Map<String, String> params);

    TiposClientesResponseDTO getById(UUID id);

    TiposClientesResponseDTO update(UUID id ,TiposClientesRequestDTO dto);

    TiposClientesResponseDTO delete(UUID dto);
}
