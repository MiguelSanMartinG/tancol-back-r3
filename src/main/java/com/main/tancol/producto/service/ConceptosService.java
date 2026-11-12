package com.main.tancol.producto.service;

import com.main.tancol.common.exeptions.IDNotFound;
import com.main.tancol.producto.dto.request.ConceptosRequestDTO;
import com.main.tancol.producto.dto.response.ConceptosResponseDTO;
import com.main.tancol.producto.entities.ConceptosEntity;
import com.main.tancol.producto.repositories.ConceptosRepository;
import com.main.tancol.producto.service.interfaces.IConceptosService;
import com.main.tancol.producto.util.ConceptosSpecification;
import com.main.tancol.producto.util.ConceptosValueMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ConceptosService implements IConceptosService {

    private final ConceptosRepository repository;


    @Override
    public ConceptosResponseDTO create(ConceptosRequestDTO dto) {
        ConceptosEntity concepto = ConceptosEntity.builder()
                .tipoRezago(dto.getTipoRezago())
                .motivo(dto.getMotivo())
                .build();
        return ConceptosValueMapper.ConceptosEntityToResponse(repository.save(concepto));
    }

    @Override
    public ConceptosResponseDTO getById(Long id) {
        ConceptosEntity entity = repository.findById(id).orElseThrow(() -> new IDNotFound("No se encontra la id proporcionada"));
        return ConceptosValueMapper.ConceptosEntityToResponse(entity);
    }

    @Override
    public ConceptosResponseDTO update(Long id, ConceptosRequestDTO dto) {
        ConceptosEntity entity = repository.findById(id).orElseThrow(() -> new IDNotFound("No se encontra la id proporcionada"));
        entity.setTipoRezago(dto.getTipoRezago());
        entity.setMotivo(dto.getMotivo());
        entity.setEnabled(false);
        return ConceptosValueMapper.ConceptosEntityToResponse(repository.save(entity));
    }

    @Override
    public ConceptosResponseDTO delete(Long id) {
        ConceptosEntity entity = repository.findById(id).orElseThrow(() -> new IDNotFound("No se encontra la id proporcionada"));
        entity.setDeleted(true);
        return ConceptosValueMapper.ConceptosEntityToResponse(repository.save(entity));
    }

    @Override
    public List<ConceptosResponseDTO> getAll(Map<String, String> params) {
        List<ConceptosEntity> list = repository.findAll(filterWithParameters(params));
        return list.stream().map(ConceptosValueMapper::ConceptosEntityToResponse).toList();
    }

    public Specification<ConceptosEntity> filterWithParameters(Map<String, String> params) {
        return new ConceptosSpecification().getSpecificationByFilters(params);
    }
}
