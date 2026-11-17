package com.main.tancol.liquidacion.service.interfaces;

import com.main.tancol.common.dto.PageResponse;
import com.main.tancol.liquidacion.dto.request.CancelacionLiquidacionRequestDTO;
import com.main.tancol.liquidacion.dto.response.LiquidacionListResponseDTO;
import com.main.tancol.liquidacion.dto.request.CancelacionPartidaRequestDTO;
import com.main.tancol.liquidacion.dto.request.LiquidacionRequestDTO;
import com.main.tancol.liquidacion.dto.response.LiquidacionResponseDTO;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ILiquidacionService {

    LiquidacionResponseDTO create(LiquidacionRequestDTO dto);

    List<LiquidacionResponseDTO> getAll(Map<String, String> params);

    PageResponse<LiquidacionListResponseDTO> getPage(Map<String, String> params);

    LiquidacionResponseDTO getById(UUID id);

    LiquidacionResponseDTO update(UUID id , LiquidacionRequestDTO dto);

    LiquidacionResponseDTO delete(UUID dto);

    LiquidacionResponseDTO cancelarPartida(UUID liquidacionId, UUID partidaId, CancelacionPartidaRequestDTO dto);

    LiquidacionResponseDTO cancelarLiquidacion(UUID id, CancelacionLiquidacionRequestDTO dto);
}
