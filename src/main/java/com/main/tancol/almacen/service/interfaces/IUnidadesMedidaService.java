package com.main.tancol.almacen.service.interfaces;

import com.main.tancol.almacen.dto.request.UnidadesMedidaRequestDTO;
import com.main.tancol.almacen.dto.response.UnidadesMedidaResponseDTO;

import java.util.*;

public interface IUnidadesMedidaService {

    UnidadesMedidaResponseDTO create(UnidadesMedidaRequestDTO dto);

    List<UnidadesMedidaResponseDTO> getAll(Map<String, String> params);

    UnidadesMedidaResponseDTO getById(UUID id);

    UnidadesMedidaResponseDTO update(UUID id ,UnidadesMedidaRequestDTO dto);

    UnidadesMedidaResponseDTO delete(UUID dto);
}
