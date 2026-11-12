package com.main.tancol.tablasamonestaciones.service.interfaces;

import com.main.tancol.tablasamonestaciones.dto.request.TablasAmonestacionesRequestDTO;
import com.main.tancol.tablasamonestaciones.dto.response.ProductoAmonestacionDisponibleResponseDTO;
import com.main.tancol.tablasamonestaciones.dto.response.TablasAmonestacionesResponseDTO;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ITablasAmonestacionesService {

    TablasAmonestacionesResponseDTO create(TablasAmonestacionesRequestDTO dto);

    List<TablasAmonestacionesResponseDTO> getAll(Map<String, String> params);

    TablasAmonestacionesResponseDTO getById(UUID id);

    TablasAmonestacionesResponseDTO update(UUID id, TablasAmonestacionesRequestDTO dto);

    TablasAmonestacionesResponseDTO delete(UUID id);

    List<ProductoAmonestacionDisponibleResponseDTO> productosDisponibles(UUID tablaId);
}
