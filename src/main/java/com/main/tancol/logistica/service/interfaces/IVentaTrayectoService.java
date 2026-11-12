package com.main.tancol.logistica.service.interfaces;

import com.main.tancol.logistica.dto.request.VentaTrayectoRequestDTO;
import com.main.tancol.logistica.dto.response.VentaTrayectoResponseDTO;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface IVentaTrayectoService {

    VentaTrayectoResponseDTO create(VentaTrayectoRequestDTO dto);

    List<VentaTrayectoResponseDTO> getAll(Map<String, String> params);

    VentaTrayectoResponseDTO getById(UUID id);

    VentaTrayectoResponseDTO update(UUID id , VentaTrayectoRequestDTO dto);

    VentaTrayectoResponseDTO delete(UUID dto);
}
