package com.main.tancol.almacen.service.interfaces;

import com.main.tancol.almacen.dto.request.partidas.PartidaOrdenInternaRequestDTO;
import com.main.tancol.almacen.dto.response.partidas.PartidaOrdenInternaResponseDTO;

import java.util.*;

public interface IPartidaOrdenInternaService {

    PartidaOrdenInternaResponseDTO create(PartidaOrdenInternaRequestDTO dto);

    List<PartidaOrdenInternaResponseDTO> getAll(Map<String, String> params);

    PartidaOrdenInternaResponseDTO getById(UUID id);

    PartidaOrdenInternaResponseDTO update(UUID id ,PartidaOrdenInternaRequestDTO dto);

    PartidaOrdenInternaResponseDTO delete(UUID dto);
}
