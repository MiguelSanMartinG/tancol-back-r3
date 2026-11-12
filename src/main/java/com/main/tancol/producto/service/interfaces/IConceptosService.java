package com.main.tancol.producto.service.interfaces;

import com.main.tancol.producto.dto.request.ConceptosRequestDTO;
import com.main.tancol.producto.dto.response.ConceptosResponseDTO;

import java.util.*;

public interface IConceptosService {

    ConceptosResponseDTO create(ConceptosRequestDTO dto);

    List<ConceptosResponseDTO> getAll(Map<String, String> params);

    ConceptosResponseDTO getById(Long id);

    ConceptosResponseDTO update(Long id ,ConceptosRequestDTO dto);

    ConceptosResponseDTO delete(Long dto);
}
