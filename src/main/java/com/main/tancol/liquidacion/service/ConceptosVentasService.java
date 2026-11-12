package com.main.tancol.liquidacion.service;

import com.main.tancol.common.exeptions.IDNotFound;
import com.main.tancol.liquidacion.dto.request.ConceptosVentasRequestDTO;
import com.main.tancol.liquidacion.dto.response.ConceptosVentasResponseDTO;
import com.main.tancol.liquidacion.entities.ConceptosVentasEntity;
import com.main.tancol.liquidacion.repositories.ConceptosVentasRepository;
import com.main.tancol.liquidacion.service.interfaces.IConceptosVentasService;
import com.main.tancol.liquidacion.util.conceptos.ConceptosVentasSpecification;
import com.main.tancol.liquidacion.util.conceptos.ConceptosVentasValueMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConceptosVentasService implements IConceptosVentasService {

    private final ConceptosVentasRepository repository;


    @Override
    public ConceptosVentasResponseDTO create(ConceptosVentasRequestDTO dto) {
        return ConceptosVentasValueMapper.ConceptosEntityToResponse(repository.save(ConceptosVentasValueMapper.ConceptosRequestToEntity(dto)));
    }

    @Override
    public ConceptosVentasResponseDTO getById(UUID id) {
        ConceptosVentasEntity entity = repository.findById(id).orElseThrow(() -> new IDNotFound("No se encontro el id " + id));
        return ConceptosVentasValueMapper.ConceptosEntityToResponse(this.repository.save(entity));
    }

    @Override
    public ConceptosVentasResponseDTO update(UUID id, ConceptosVentasRequestDTO dto) {
        ConceptosVentasEntity entity = repository.findById(id).orElseThrow(() -> new IDNotFound("No se encontro el id " + id));
        entity.setConcepto(dto.getConcepto());
        entity.setDescuento(dto.isDescuento());
        entity.setExclude(dto.isExclude());
        entity.setName(dto.getName());
        entity.setEnabled(dto.isEnabled());
        entity.setCancelado(dto.isCancelado());
        return ConceptosVentasValueMapper.ConceptosEntityToResponse(repository.save(entity));
    }

    @Override
    public ConceptosVentasResponseDTO delete(UUID id) {
        ConceptosVentasEntity entity = repository.findById(id).orElseThrow(() -> new IDNotFound("No se encontro el id " + id));
        entity.setDeleted(true);
        return ConceptosVentasValueMapper.ConceptosEntityToResponse(repository.save(entity));
    }

    @Override
    public List<ConceptosVentasResponseDTO> getAll(Map<String, String> params) {
        List<ConceptosVentasEntity> list = repository.findAll(filterWithParameters(params));
        return list.stream().map(ConceptosVentasValueMapper::ConceptosEntityToResponse).toList();
    }

    public Specification<ConceptosVentasEntity> filterWithParameters(Map<String, String> params) {
        return new ConceptosVentasSpecification().getSpecificationByFilters(params);
    }
}
