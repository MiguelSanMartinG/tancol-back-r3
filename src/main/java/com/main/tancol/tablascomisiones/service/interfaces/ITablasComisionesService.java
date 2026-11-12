package com.main.tancol.tablascomisiones.service.interfaces;

import com.main.tancol.tablascomisiones.dto.request.TablasComisionesRequestDTO;
import com.main.tancol.tablascomisiones.dto.response.ProductoComisionDisponibleResponseDTO;
import com.main.tancol.tablascomisiones.dto.response.TablasComisionesResponseDTO;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ITablasComisionesService {

    TablasComisionesResponseDTO create(TablasComisionesRequestDTO dto);

    List<TablasComisionesResponseDTO> getAll(Map<String, String> params);

    TablasComisionesResponseDTO getById(UUID id);

    TablasComisionesResponseDTO update(UUID id, TablasComisionesRequestDTO dto);

    TablasComisionesResponseDTO delete(UUID id);

    List<ProductoComisionDisponibleResponseDTO> productosDisponibles(UUID tablaId);
}
