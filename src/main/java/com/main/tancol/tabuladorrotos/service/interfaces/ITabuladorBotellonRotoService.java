package com.main.tancol.tabuladorrotos.service.interfaces;

import com.main.tancol.tabuladorrotos.dto.request.TabuladorBotellonRotoRequestDTO;
import com.main.tancol.tabuladorrotos.dto.response.TabuladorBotellonRotoResponseDTO;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ITabuladorBotellonRotoService {

    TabuladorBotellonRotoResponseDTO create(TabuladorBotellonRotoRequestDTO dto);

    List<TabuladorBotellonRotoResponseDTO> getAll(Map<String, String> params);

    TabuladorBotellonRotoResponseDTO getById(UUID id);

    TabuladorBotellonRotoResponseDTO update(UUID id, TabuladorBotellonRotoRequestDTO dto);

    TabuladorBotellonRotoResponseDTO delete(UUID id);
}
