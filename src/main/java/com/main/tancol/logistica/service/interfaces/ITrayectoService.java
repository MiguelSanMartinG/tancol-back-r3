package com.main.tancol.logistica.service.interfaces;

import com.main.tancol.logistica.dto.request.TrayectoRequestDTO;
import com.main.tancol.logistica.dto.response.TrayectoResponseDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ITrayectoService {

    TrayectoResponseDTO create(TrayectoRequestDTO dto);

    List<TrayectoResponseDTO> getAll(Map<String, String> params);

    TrayectoResponseDTO getById(UUID id);

    TrayectoResponseDTO update(UUID id ,TrayectoRequestDTO dto);

    ResponseEntity<?> delete(UUID dto);
}
