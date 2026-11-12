package com.main.tancol.liquidacion.service.interfaces;

import com.main.tancol.liquidacion.dto.request.ConceptosVentasRequestDTO;
import com.main.tancol.liquidacion.dto.response.ConceptosVentasResponseDTO;

import java.util.*;

public interface IConceptosVentasService {

    ConceptosVentasResponseDTO create(ConceptosVentasRequestDTO dto);

    List<ConceptosVentasResponseDTO> getAll(Map<String, String> params);

    ConceptosVentasResponseDTO getById(UUID id);

    ConceptosVentasResponseDTO update(UUID id , ConceptosVentasRequestDTO dto);

    ConceptosVentasResponseDTO delete(UUID dto);
}
