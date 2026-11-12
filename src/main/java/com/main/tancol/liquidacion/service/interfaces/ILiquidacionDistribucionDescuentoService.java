package com.main.tancol.liquidacion.service.interfaces;

import com.main.tancol.liquidacion.dto.request.LiquidacionDistribucionDescuentoRequestDTO;
import com.main.tancol.liquidacion.dto.response.LiquidacionDistribucionDescuentoResponseDTO;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ILiquidacionDistribucionDescuentoService {

    LiquidacionDistribucionDescuentoResponseDTO create(LiquidacionDistribucionDescuentoRequestDTO dto);

    List<LiquidacionDistribucionDescuentoResponseDTO> getAll(Map<String, String> params);

    LiquidacionDistribucionDescuentoResponseDTO getById(UUID id);

    LiquidacionDistribucionDescuentoResponseDTO update(UUID id ,LiquidacionDistribucionDescuentoRequestDTO dto);

    LiquidacionDistribucionDescuentoResponseDTO delete(UUID dto);
}
