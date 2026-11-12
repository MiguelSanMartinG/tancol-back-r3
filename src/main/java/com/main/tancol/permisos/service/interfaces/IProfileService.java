package com.main.tancol.permisos.service.interfaces;

import com.main.tancol.permisos.dto.request.ProfileRequestDTO;
import com.main.tancol.permisos.dto.response.ProfileResponseDTO;

import java.util.*;

public interface IProfileService {

    ProfileResponseDTO create(ProfileRequestDTO dto);

    List<ProfileResponseDTO> getAll(Map<String, String> params);

    ProfileResponseDTO getById(UUID id);

    ProfileResponseDTO update(UUID id ,ProfileRequestDTO dto);

    ProfileResponseDTO delete(UUID dto);
}
