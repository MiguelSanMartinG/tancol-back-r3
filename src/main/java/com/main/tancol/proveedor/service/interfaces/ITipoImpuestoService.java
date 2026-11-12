package com.main.tancol.proveedor.service.interfaces;

import com.main.tancol.proveedor.dto.request.TipoImpuestoRequestDTO;
import com.main.tancol.proveedor.dto.response.TipoImpuestoResponseDTO;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ITipoImpuestoService {

        TipoImpuestoResponseDTO create(TipoImpuestoRequestDTO dto);

        List<TipoImpuestoResponseDTO> getAll(Map<String, String> params);

        TipoImpuestoResponseDTO getById(UUID id);

        TipoImpuestoResponseDTO update(UUID id ,TipoImpuestoRequestDTO dto);

        TipoImpuestoResponseDTO delete(UUID dto);
}
