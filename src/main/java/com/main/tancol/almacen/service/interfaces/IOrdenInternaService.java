package com.main.tancol.almacen.service.interfaces;

import com.main.tancol.almacen.dto.request.OrdenInternaRequestDTO;
import com.main.tancol.almacen.dto.response.OrdenInternaResponseDTO;

import java.util.*;

public interface IOrdenInternaService {

    OrdenInternaResponseDTO create(OrdenInternaRequestDTO dto);

    List<OrdenInternaResponseDTO> getAll(Map<String, String> params);

    OrdenInternaResponseDTO getById(UUID id);

    OrdenInternaResponseDTO update(UUID id ,OrdenInternaRequestDTO dto);

    OrdenInternaResponseDTO delete(UUID dto);
}
