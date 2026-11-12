package com.main.tancol.prestamo.service.interfaces;

import com.main.tancol.common.dto.PageResponse;
import com.main.tancol.prestamo.dto.request.EquipoRequestDTO;
import com.main.tancol.prestamo.dto.response.EquipoResponseDTO;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface IEquipoService {

    EquipoResponseDTO create(EquipoRequestDTO dto);

    List<EquipoResponseDTO> getAll(Map<String, String> params);

    PageResponse<EquipoResponseDTO> getPage(Map<String, String> params);

    EquipoResponseDTO getById(UUID id);

    EquipoResponseDTO update(UUID id, EquipoRequestDTO dto);

    EquipoResponseDTO delete(UUID id);
}
