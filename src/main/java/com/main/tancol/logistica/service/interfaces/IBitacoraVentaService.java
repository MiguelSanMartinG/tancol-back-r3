package com.main.tancol.logistica.service.interfaces;

import com.main.tancol.logistica.dto.request.BitacoraVentaRequestDTO;
import com.main.tancol.logistica.dto.response.BitacoraVentaResponseDTO;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface IBitacoraVentaService {

    BitacoraVentaResponseDTO create(BitacoraVentaRequestDTO dto);

    List<BitacoraVentaResponseDTO> getAll(Map<String, String> params);

    BitacoraVentaResponseDTO getById(UUID id);

    BitacoraVentaResponseDTO update(UUID id, BitacoraVentaRequestDTO dto);

    BitacoraVentaResponseDTO delete(UUID dto);
}
