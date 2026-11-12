package com.main.tancol.liquidacion.service.interfaces;

import com.main.tancol.liquidacion.dto.request.LiquidacionTotalizadoEmpleadoRequestDTO;
import com.main.tancol.liquidacion.dto.response.LiquidacionTotalizadoEmpleadoResponseDTO;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface LiquidacionTotalizadoEmpleadoService {

    LiquidacionTotalizadoEmpleadoResponseDTO create(LiquidacionTotalizadoEmpleadoRequestDTO dto);

    List<LiquidacionTotalizadoEmpleadoResponseDTO> getAll(Map<String, String> params);

    LiquidacionTotalizadoEmpleadoResponseDTO getById(UUID id);

    LiquidacionTotalizadoEmpleadoResponseDTO update(UUID id , LiquidacionTotalizadoEmpleadoRequestDTO dto);

    LiquidacionTotalizadoEmpleadoResponseDTO delete(UUID dto);
}
