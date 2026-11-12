package com.main.tancol.cliente.service;

import com.main.tancol.cliente.dto.request.TiposClientesRequestDTO;
import com.main.tancol.cliente.dto.response.TiposClientesResponseDTO;
import com.main.tancol.cliente.entities.TipoClienteEntity;
import com.main.tancol.cliente.repositories.TiposClientesRepository;
import com.main.tancol.cliente.util.ClienteValueMapper;
import com.main.tancol.cliente.util.TiposClientesValueMapper;
import com.main.tancol.empleados.util.TiposEmpleadosValueMapper;
import org.flywaydb.core.internal.database.DatabaseType;
import org.springframework.stereotype.Service;
import org.springframework.data.jpa.domain.Specification;
import com.main.tancol.cliente.util.TiposClientesSpecification;
import com.main.tancol.cliente.service.interfaces.ITiposClientesService;

import java.util.*;

@Service
public class TiposClientesService implements ITiposClientesService {

    private final TiposClientesRepository repository;

    public TiposClientesService(TiposClientesRepository repository) {
        this.repository = repository;
    }


    @Override
    public TiposClientesResponseDTO create(TiposClientesRequestDTO dto) {


        TipoClienteEntity newTipoEntity = TiposClientesValueMapper.TiposClientesRequestToEntity(dto);

        this.repository.save(newTipoEntity);
        return TiposClientesValueMapper.TiposClientesEntityToResponse(newTipoEntity);
    }

    @Override
    public TiposClientesResponseDTO getById(UUID id) {
        TipoClienteEntity query = this.repository.findById(id).orElseThrow(() -> new RuntimeException("No se encontro el tipo de cliente"));
        return TiposClientesValueMapper.TiposClientesEntityToResponse(query);
    }

    @Override
    public TiposClientesResponseDTO update(UUID id, TiposClientesRequestDTO dto) {
        TipoClienteEntity query = this.repository.findById(id).orElseThrow(
                () -> new RuntimeException("No se encontro el tipo de cliente"));
        query.setNombre(dto.getNombre());
        query.setAlias(dto.getAlias());
        return TiposClientesValueMapper.TiposClientesEntityToResponse(repository.save(query));
    }

    @Override
    public TiposClientesResponseDTO delete(UUID id) {
        TipoClienteEntity query = this.repository.findById(id).orElseThrow(
                () -> new RuntimeException("No se encontro el tipo de cliente"));
        query.setDeleted(true);
        return TiposClientesValueMapper.TiposClientesEntityToResponse(repository.save(query));
    }

    @Override
    public List<TiposClientesResponseDTO> getAll(Map<String, String> params) {
        List<TipoClienteEntity> list = repository.findAll(filterWithParameters(params));
        return list.stream().map(TiposClientesValueMapper::TiposClientesEntityToResponse).toList();
    }

    public Specification<TipoClienteEntity> filterWithParameters(Map<String, String> params) {
        return new TiposClientesSpecification().getSpecificationByFilters(params);
    }
}
