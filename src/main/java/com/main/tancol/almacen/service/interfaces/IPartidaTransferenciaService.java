package com.main.tancol.almacen.service.interfaces;

import com.main.tancol.almacen.dto.request.partidas.PartidaTransferenciaRequestDTO;
import com.main.tancol.almacen.dto.response.partidas.PartidaTransferenciaResponseDTO;

import java.util.*;

public interface IPartidaTransferenciaService {

    PartidaTransferenciaResponseDTO create(PartidaTransferenciaRequestDTO dto);

    List<PartidaTransferenciaResponseDTO> getAll(Map<String, String> params);

    PartidaTransferenciaResponseDTO getById(UUID id);

    PartidaTransferenciaResponseDTO update(UUID id ,PartidaTransferenciaRequestDTO dto);

    PartidaTransferenciaResponseDTO delete(UUID dto);
}
