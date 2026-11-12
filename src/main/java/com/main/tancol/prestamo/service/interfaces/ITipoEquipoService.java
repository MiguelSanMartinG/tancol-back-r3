package com.main.tancol.prestamo.service.interfaces;

import com.main.tancol.prestamo.dto.request.TipoEquipoRequestDTO;
import com.main.tancol.prestamo.dto.response.TipoEquipoResponseDTO;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ITipoEquipoService {

    TipoEquipoResponseDTO create(TipoEquipoRequestDTO dto);

    List<TipoEquipoResponseDTO> getAll(Map<String, String> params);

    TipoEquipoResponseDTO getById(UUID id);

    TipoEquipoResponseDTO update(UUID id, TipoEquipoRequestDTO dto);

    TipoEquipoResponseDTO delete(UUID id);
}
