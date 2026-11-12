package com.main.tancol.orden.service.interfaces;

import com.main.tancol.orden.dto.request.PartidaOrdenRequestDTO;
import com.main.tancol.orden.dto.response.PartidaOrdenResponseDTO;
import com.main.tancol.orden.entities.OrdenEntity;
import com.main.tancol.orden.entities.PartidaOrdenEntity;

import java.util.*;

public interface IPartidaOrdenService {


    List<PartidaOrdenResponseDTO> getAll(Map<String, String> params);

    PartidaOrdenEntity create(PartidaOrdenRequestDTO dto, OrdenEntity ordenEntity);

    PartidaOrdenResponseDTO getById(UUID id);

    PartidaOrdenResponseDTO update(UUID id , PartidaOrdenRequestDTO dto);

    PartidaOrdenResponseDTO delete(UUID dto);
}
