package com.main.tancol.almacen.service;

import com.main.tancol.almacen.dto.request.UnidadesMedidaRequestDTO;
import com.main.tancol.almacen.dto.response.UnidadesMedidaResponseDTO;
import com.main.tancol.almacen.entities.UnidadesMedidaEntity;
import com.main.tancol.almacen.repositories.UnidadesMedidaRepository;
import com.main.tancol.almacen.service.interfaces.IUnidadesMedidaService;
import com.main.tancol.almacen.util.UnidadesMedidaSpecification;
import com.main.tancol.almacen.util.UnidadesMedidaValueMapper;
import com.main.tancol.common.exeptions.IDNotFound;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class UnidadesMedidaService implements IUnidadesMedidaService {

    private final UnidadesMedidaRepository repository;

    public UnidadesMedidaService(UnidadesMedidaRepository repository) {
        this.repository = repository;
    }


    @Override
    public UnidadesMedidaResponseDTO create(UnidadesMedidaRequestDTO dto) {
        UnidadesMedidaEntity entity = UnidadesMedidaValueMapper.UnidadesMedidaRequestToEntity(dto);

        return UnidadesMedidaValueMapper.UnidadesMedidaEntityToResponse(this.repository.save(entity));
    }

    @Override
    public UnidadesMedidaResponseDTO getById(UUID id) {
        return UnidadesMedidaValueMapper.UnidadesMedidaEntityToResponse(this.repository.findById(id).orElseThrow(() -> new IDNotFound("No se encontro nada con el ID buscado")));
    }

    @Override
    public UnidadesMedidaResponseDTO update(UUID id, UnidadesMedidaRequestDTO dto) {
        UnidadesMedidaEntity query = this.repository.findById(id).orElseThrow(() -> new IDNotFound("No se una unidad de medida con esa ID"));
        query.setAlias(dto.getAlias());
        query.setNombre(dto.getNombre());
        return UnidadesMedidaValueMapper.UnidadesMedidaEntityToResponse(this.repository.save(query));
    }

    @Override
    public UnidadesMedidaResponseDTO delete(UUID id) {
        UnidadesMedidaEntity query = this.repository.findById(id).orElseThrow(() -> new IDNotFound("No se una unidad de medida con esa ID"));
        query.setDeleted(true);
        return UnidadesMedidaValueMapper.UnidadesMedidaEntityToResponse(this.repository.save(query));
    }

    @Override
    public List<UnidadesMedidaResponseDTO> getAll(Map<String, String> params) {
        List<UnidadesMedidaEntity> list = repository.findAll(filterWithParameters(params));
        return list.stream().map(UnidadesMedidaValueMapper::UnidadesMedidaEntityToResponse).toList();
    }

    public Specification<UnidadesMedidaEntity> filterWithParameters(Map<String, String> params) {
        return new UnidadesMedidaSpecification().getSpecificationByFilters(params);
    }
}
