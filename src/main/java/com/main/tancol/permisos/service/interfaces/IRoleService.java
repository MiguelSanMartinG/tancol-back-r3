package com.main.tancol.permisos.service.interfaces;

import com.main.tancol.permisos.dto.request.RoleRequestDTO;
import com.main.tancol.permisos.dto.response.RoleResponseDTO;

import java.util.List;
import java.util.Map;


public interface IRoleService {

    RoleResponseDTO create(RoleRequestDTO dto);

    List<RoleResponseDTO> getAll(Map<String, String> params);

    RoleResponseDTO getById(Long id);

    RoleResponseDTO update(Long id ,RoleRequestDTO dto);

    RoleResponseDTO delete(Long dto);
}
