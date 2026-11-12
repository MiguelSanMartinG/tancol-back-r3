package com.main.tancol.liquidacion.service.interfaces;

import com.main.tancol.liquidacion.dto.request.ReparticionEntreCuentasRequestDTO;
import com.main.tancol.liquidacion.dto.response.ReparticionEntreCuentasResponseDTO;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface IReparticionEntreCuentasService {

    ReparticionEntreCuentasResponseDTO create(ReparticionEntreCuentasRequestDTO dto);

    List<ReparticionEntreCuentasResponseDTO> getAll(Map<String, String> params);

    ReparticionEntreCuentasResponseDTO getById(UUID id);

    ReparticionEntreCuentasResponseDTO update(UUID id ,ReparticionEntreCuentasRequestDTO dto);

    ReparticionEntreCuentasResponseDTO delete(UUID dto);
}
