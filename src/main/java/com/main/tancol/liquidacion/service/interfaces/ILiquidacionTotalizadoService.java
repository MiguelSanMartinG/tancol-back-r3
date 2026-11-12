package com.main.tancol.liquidacion.service.interfaces;

import com.main.tancol.liquidacion.dto.request.LiquidacionTotalizadoRequestDTO;
import com.main.tancol.liquidacion.dto.response.LiquidacionTotalizadoResponseDTO;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ILiquidacionTotalizadoService {

    LiquidacionTotalizadoResponseDTO create(LiquidacionTotalizadoRequestDTO dto);

    List<LiquidacionTotalizadoResponseDTO> getAll(Map<String, String> params);

    LiquidacionTotalizadoResponseDTO getById(UUID id);

    LiquidacionTotalizadoResponseDTO update(UUID id , LiquidacionTotalizadoRequestDTO dto);

    LiquidacionTotalizadoResponseDTO delete(UUID dto);
}
