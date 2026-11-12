package com.main.tancol.empleados.service.interfaces;

import com.main.tancol.common.dto.PageResponse;
import com.main.tancol.empleados.dto.request.DepartamentoRequestDTO;
import com.main.tancol.empleados.dto.response.DepartamentoResponseDTO;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface IDepartamentoService {
    DepartamentoResponseDTO create(DepartamentoRequestDTO dto);

    List<DepartamentoResponseDTO> getAll(Map<String, String> params);

    PageResponse<DepartamentoResponseDTO> getPage(Map<String, String> params);

    DepartamentoResponseDTO getById(UUID id);

    DepartamentoResponseDTO update(UUID id , DepartamentoRequestDTO dto);

    DepartamentoResponseDTO delete(UUID dto);
}
