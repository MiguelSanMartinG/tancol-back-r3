package com.main.tancol.prestamo.service.interfaces;

import com.main.tancol.prestamo.dto.request.EquipoAsignacionRequestDTO;
import com.main.tancol.prestamo.dto.response.EquipoAsignacionResponseDTO;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface IEquipoAsignacionService {

    EquipoAsignacionResponseDTO create(EquipoAsignacionRequestDTO dto);

    List<EquipoAsignacionResponseDTO> getAll(Map<String, String> params);

    EquipoAsignacionResponseDTO getById(UUID id);

    EquipoAsignacionResponseDTO devolver(UUID id);
}
