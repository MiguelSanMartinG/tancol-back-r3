package com.main.tancol.medidores.service.interfaces;

import com.main.tancol.medidores.dto.request.BitacoraMedicionRequestDTO;
import com.main.tancol.medidores.dto.response.BitacoraMedicionResponseDTO;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface IBitacoraMedicionService {

    BitacoraMedicionResponseDTO create(BitacoraMedicionRequestDTO dto);

    List<BitacoraMedicionResponseDTO> getAll(Map<String, String> params);

    BitacoraMedicionResponseDTO getById(UUID id);

    BitacoraMedicionResponseDTO update(UUID id ,BitacoraMedicionRequestDTO dto);

    BitacoraMedicionResponseDTO delete(UUID dto);
}
