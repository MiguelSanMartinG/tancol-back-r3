package com.main.tancol.proveedor.service;

import com.main.tancol.common.exeptions.IDNotFound;
import com.main.tancol.proveedor.dto.request.TipoProveedorRequestDTO;
import com.main.tancol.proveedor.dto.response.TipoProveedorResponseDTO;
import com.main.tancol.proveedor.repositories.TipoProveedorRepository;
import com.main.tancol.proveedor.util.TipoProveedorValueMapper;
import org.springframework.stereotype.Service;
import org.springframework.data.jpa.domain.Specification;
import com.main.tancol.proveedor.entity.TipoProveedorEntity;
import com.main.tancol.proveedor.util.TipoProveedorSpecification;
import com.main.tancol.proveedor.service.interfaces.ITipoProveedorService;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TipoProveedorService implements ITipoProveedorService {

    private final TipoProveedorRepository repository;

    public TipoProveedorService(TipoProveedorRepository repository) {
        this.repository = repository;
    }


    @Override
    public TipoProveedorResponseDTO create(TipoProveedorRequestDTO dto) {
        TipoProveedorEntity n = TipoProveedorEntity.builder().nombre(dto.getNombre()).alias(dto.getAlias()).build();
        return TipoProveedorValueMapper.TipoProveedorEntityToResponse(this.repository.save(n));
    }

    @Override
    public TipoProveedorResponseDTO getById(UUID id) {
        TipoProveedorEntity tipo = this.repository.findById(id).orElseThrow(() -> new IDNotFound("No existe el tipo de proveedor"));
        return TipoProveedorValueMapper.TipoProveedorEntityToResponse(this.repository.save(tipo));
    }

    @Override
    public TipoProveedorResponseDTO update(UUID id, TipoProveedorRequestDTO dto) {
        TipoProveedorEntity tipo = this.repository.findById(id).orElseThrow(() -> new IDNotFound("No existe el tipo de proveedor"));
        tipo.setNombre(dto.getNombre());
        tipo.setAlias(dto.getAlias());
        return TipoProveedorValueMapper.TipoProveedorEntityToResponse(this.repository.save(tipo));
    }

    @Override
    public TipoProveedorResponseDTO delete(UUID id) {
        TipoProveedorEntity tipo = this.repository.findById(id).orElseThrow(() -> new IDNotFound("No existe el tipo de proveedor"));
        tipo.setDeleted(true);
        return TipoProveedorValueMapper.TipoProveedorEntityToResponse(this.repository.save(tipo));
    }

    @Override
    public List<TipoProveedorResponseDTO> getAll(Map<String, String> params) {
        List<TipoProveedorEntity> list = repository.findAll(filterWithParameters(params));
        List<TipoProveedorResponseDTO> response = list.stream().map(TipoProveedorValueMapper::TipoProveedorEntityToResponse).toList();
        return response;
    }

    public Specification<TipoProveedorEntity> filterWithParameters(Map<String, String> params) {
        return new TipoProveedorSpecification().getSpecificationByFilters(params);
    }
}
