package com.main.tancol.solicitudes.service.interfaces;

import com.main.tancol.solicitudes.dto.request.SolicitudesRequestDTO;
import com.main.tancol.solicitudes.dto.response.SolicitudesResponseDTO;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ISolicitudesService {

    SolicitudesResponseDTO create(SolicitudesRequestDTO dto);

    List<SolicitudesResponseDTO> getAll(Map<String, String> params);

    SolicitudesResponseDTO getById(UUID id);

    SolicitudesResponseDTO update(UUID id ,SolicitudesRequestDTO dto);

    SolicitudesResponseDTO delete(UUID dto);
}
