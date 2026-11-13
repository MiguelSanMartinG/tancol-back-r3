package com.main.tancol.metaventa.service.interfaces;

import com.main.tancol.metaventa.dto.request.MetaVentaRutaRequestDTO;
import com.main.tancol.metaventa.dto.request.MetaVentaUpdateRequestDTO;
import com.main.tancol.metaventa.dto.response.MetaVentaRutaResponseDTO;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface IMetaVentaRutaService {

    MetaVentaRutaResponseDTO create(MetaVentaRutaRequestDTO dto);

    List<MetaVentaRutaResponseDTO> getAll(Map<String, String> params);

    MetaVentaRutaResponseDTO getById(UUID id);

    MetaVentaRutaResponseDTO update(UUID id, MetaVentaUpdateRequestDTO dto);

    MetaVentaRutaResponseDTO cerrar(UUID id);
}
