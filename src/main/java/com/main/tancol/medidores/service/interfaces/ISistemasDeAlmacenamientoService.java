package com.main.tancol.medidores.service.interfaces;

import com.main.tancol.medidores.dto.request.SistemasDeAlmacenamientoRequestDTO;
import com.main.tancol.medidores.dto.response.SistemasDeAlmacenamientoResponseDTO;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ISistemasDeAlmacenamientoService {

    SistemasDeAlmacenamientoResponseDTO create(SistemasDeAlmacenamientoRequestDTO dto);

    List<SistemasDeAlmacenamientoResponseDTO> getAll(Map<String, String> params);

    SistemasDeAlmacenamientoResponseDTO getById(UUID id);

    SistemasDeAlmacenamientoResponseDTO update(UUID id ,SistemasDeAlmacenamientoRequestDTO dto);

    SistemasDeAlmacenamientoResponseDTO delete(UUID dto);
}
