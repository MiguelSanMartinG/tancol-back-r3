package com.main.tancol.metaventa.service.interfaces;

import com.main.tancol.metaventa.dto.request.MetaVentaRequestDTO;
import com.main.tancol.metaventa.dto.request.MetaVentaUpdateRequestDTO;
import com.main.tancol.metaventa.dto.response.MetaVentaResponseDTO;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface IMetaVentaService {

    MetaVentaResponseDTO create(MetaVentaRequestDTO dto);

    List<MetaVentaResponseDTO> getAll(Map<String, String> params);

    MetaVentaResponseDTO getById(UUID id);

    MetaVentaResponseDTO update(UUID id, MetaVentaUpdateRequestDTO dto);

    MetaVentaResponseDTO cerrar(UUID id);
}
