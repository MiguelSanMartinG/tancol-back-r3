package com.main.tancol.logistica.service.interfaces;

import com.main.tancol.logistica.dto.request.partidas.BitacoraPartidaVentasRequestDTO;
import com.main.tancol.logistica.dto.response.BitacoraPartidaVentasResponseDTO;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface IBitacoraPartidaVentasService {

    BitacoraPartidaVentasResponseDTO create(BitacoraPartidaVentasRequestDTO dto);

    List<BitacoraPartidaVentasResponseDTO> getAll(Map<String, String> params);

    BitacoraPartidaVentasResponseDTO getById(UUID id);

    BitacoraPartidaVentasResponseDTO update(UUID id ,BitacoraPartidaVentasRequestDTO dto);

    BitacoraPartidaVentasResponseDTO delete(UUID dto);
}
