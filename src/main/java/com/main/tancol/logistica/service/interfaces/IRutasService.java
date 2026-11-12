package com.main.tancol.logistica.service.interfaces;

import com.main.tancol.logistica.dto.request.RutasRequestDTO;
import com.main.tancol.logistica.dto.response.RutasResponseDTO;

import java.util.*;

public interface IRutasService {

    RutasResponseDTO create(RutasRequestDTO dto);

    List<RutasResponseDTO> getAll(Map<String, String> params);

    RutasResponseDTO getById(UUID id);

    RutasResponseDTO update(UUID id ,RutasRequestDTO dto);

    RutasResponseDTO delete(UUID dto);
}
