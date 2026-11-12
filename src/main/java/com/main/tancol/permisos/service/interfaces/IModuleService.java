package com.main.tancol.permisos.service.interfaces;

import com.main.tancol.permisos.dto.request.ModuleRequestDTO;
import com.main.tancol.permisos.dto.response.ModuleResponseDTO;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface IModuleService {

    ModuleResponseDTO create(ModuleRequestDTO dto);

    List<ModuleResponseDTO> getAll(Map<String, String> params);

    ModuleResponseDTO getById(UUID id);

    ModuleResponseDTO update(UUID id ,ModuleRequestDTO dto);

    ModuleResponseDTO delete(UUID dto);
}
