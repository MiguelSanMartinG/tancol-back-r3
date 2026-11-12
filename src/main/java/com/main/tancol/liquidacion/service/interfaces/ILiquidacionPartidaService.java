package com.main.tancol.liquidacion.service.interfaces;

import com.main.tancol.liquidacion.dto.request.LiquidacionPartidaRequestDTO;
import com.main.tancol.liquidacion.dto.response.LiquidacionPartidaResponseDTO;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ILiquidacionPartidaService {

    LiquidacionPartidaResponseDTO create(LiquidacionPartidaRequestDTO dto);

    List<LiquidacionPartidaResponseDTO> getAll(Map<String, String> params);

    LiquidacionPartidaResponseDTO getById(UUID id);

    LiquidacionPartidaResponseDTO update(UUID id ,LiquidacionPartidaRequestDTO dto);

    LiquidacionPartidaResponseDTO delete(UUID dto);
}
