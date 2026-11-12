package com.main.tancol.prestamo.service;

import com.main.tancol.common.exeptions.IDNotFound;
import com.main.tancol.prestamo.dto.request.TipoEquipoRequestDTO;
import com.main.tancol.prestamo.dto.response.TipoEquipoResponseDTO;
import com.main.tancol.prestamo.entities.TipoEquipoEntity;
import com.main.tancol.prestamo.repositories.TipoEquipoRepository;
import com.main.tancol.prestamo.service.interfaces.ITipoEquipoService;
import com.main.tancol.prestamo.util.TipoEquipoSpecification;
import com.main.tancol.prestamo.util.TipoEquipoValueMapper;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class TipoEquipoService implements ITipoEquipoService {

    private final TipoEquipoRepository repository;

    public TipoEquipoService(TipoEquipoRepository repository) {
        this.repository = repository;
    }

    @Override
    public TipoEquipoResponseDTO create(TipoEquipoRequestDTO dto) {
        TipoEquipoEntity entity = TipoEquipoValueMapper.requestToEntity(dto);
        return TipoEquipoValueMapper.entityToResponse(repository.save(entity));
    }

    @Override
    public TipoEquipoResponseDTO getById(UUID id) {
        TipoEquipoEntity entity = repository.findById(id)
                .orElseThrow(() -> new IDNotFound("No se encontró el tipo de equipo"));
        return TipoEquipoValueMapper.entityToResponse(entity);
    }

    @Override
    public TipoEquipoResponseDTO update(UUID id, TipoEquipoRequestDTO dto) {
        TipoEquipoEntity entity = repository.findById(id)
                .orElseThrow(() -> new IDNotFound("No se encontró el tipo de equipo"));
        entity.setNombre(dto.getNombre());
        entity.setAlias(dto.getAlias());
        return TipoEquipoValueMapper.entityToResponse(repository.save(entity));
    }

    @Override
    public TipoEquipoResponseDTO delete(UUID id) {
        TipoEquipoEntity entity = repository.findById(id)
                .orElseThrow(() -> new IDNotFound("No se encontró el tipo de equipo"));
        entity.setDeleted(true);
        return TipoEquipoValueMapper.entityToResponse(repository.save(entity));
    }

    @Override
    public List<TipoEquipoResponseDTO> getAll(Map<String, String> params) {
        return repository.findAll(filterWithParameters(params)).stream()
                .map(TipoEquipoValueMapper::entityToResponse)
                .toList();
    }

    private Specification<TipoEquipoEntity> filterWithParameters(Map<String, String> params) {
        return new TipoEquipoSpecification().getSpecificationByFilters(params);
    }
}
