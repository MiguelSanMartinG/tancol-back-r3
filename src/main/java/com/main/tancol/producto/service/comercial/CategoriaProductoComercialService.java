package com.main.tancol.producto.service.comercial;

import com.main.tancol.common.exeptions.IDNotFound;
import com.main.tancol.producto.dto.request.comercial.CategoriaProductoComercialRequestDTO;
import com.main.tancol.producto.dto.response.comercial.CategoriaProductoComercialResponseDTO;
import com.main.tancol.producto.entities.comerciales.CategoriaProductoComercialEntity;
import com.main.tancol.producto.repositories.CategoriaProductoComercialRepository;
import com.main.tancol.producto.service.interfaces.ICategoriaProductoComercialService;
import com.main.tancol.producto.util.comercial.CategoriaProductoComercialSpecification;
import com.main.tancol.producto.util.comercial.CategoriaProductoComercialValueMapper;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class CategoriaProductoComercialService implements ICategoriaProductoComercialService {

    private final CategoriaProductoComercialRepository repository;

    public CategoriaProductoComercialService(CategoriaProductoComercialRepository repository) {
        this.repository = repository;
    }


    @Override
    public CategoriaProductoComercialResponseDTO create(CategoriaProductoComercialRequestDTO dto) {
        CategoriaProductoComercialEntity entity = CategoriaProductoComercialValueMapper.CategoriaProductoComercialRequestToEntity(dto);
        return CategoriaProductoComercialValueMapper.CategoriaProductoComercialEntityToResponse(repository.save(entity));
    }

    @Override
    public CategoriaProductoComercialResponseDTO getById(UUID id) {
        CategoriaProductoComercialEntity entity = repository.findById(id).orElseThrow(() -> new IDNotFound("No se encontró la la categoria"));
        return CategoriaProductoComercialValueMapper.CategoriaProductoComercialEntityToResponse(entity);
    }

    @Override
    public CategoriaProductoComercialResponseDTO update(UUID id, CategoriaProductoComercialRequestDTO dto) {
        CategoriaProductoComercialEntity entity = repository.findById(id).orElseThrow(() -> new IDNotFound("No se encontró la la categoria"));
        entity.setNombre(dto.getNombre());
        entity.setEnabled(dto.getEnabled());
        return CategoriaProductoComercialValueMapper.CategoriaProductoComercialEntityToResponse(repository.save(entity));
    }

    @Override
    public CategoriaProductoComercialResponseDTO delete(UUID id) {
        CategoriaProductoComercialEntity entity = repository.findById(id).orElseThrow(() -> new IDNotFound("No se encontró la la categoria"));
        entity.setDeleted(true);
        return CategoriaProductoComercialValueMapper.CategoriaProductoComercialEntityToResponse(repository.save(entity));
    }

    @Override
    public List<CategoriaProductoComercialResponseDTO> getAll(Map<String, String> params) {
        List<CategoriaProductoComercialEntity> list = repository.findAll(filterWithParameters(params));
        return list.stream().map(CategoriaProductoComercialValueMapper::CategoriaProductoComercialEntityToResponse).toList();
    }

    public Specification<CategoriaProductoComercialEntity> filterWithParameters(Map<String, String> params) {
        return new CategoriaProductoComercialSpecification().getSpecificationByFilters(params);
    }
}
