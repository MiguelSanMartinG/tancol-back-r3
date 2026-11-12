package com.main.tancol.orden.service.interfaces;

import com.main.tancol.orden.dto.request.SolicitudOrdenRequestDTO;
import com.main.tancol.orden.dto.response.SolicitudOrdenResponseDTO;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface IOrdenService {

    SolicitudOrdenResponseDTO create(SolicitudOrdenRequestDTO dto);

    List<SolicitudOrdenResponseDTO> getAll(Map<String, String> params);

    SolicitudOrdenResponseDTO getById(UUID id);

    SolicitudOrdenResponseDTO update(UUID id , SolicitudOrdenRequestDTO dto);

    SolicitudOrdenResponseDTO delete(UUID dto);
}
