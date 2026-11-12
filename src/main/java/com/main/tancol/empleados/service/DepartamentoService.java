package com.main.tancol.empleados.service;


import com.main.tancol.common.dto.CatalogPage;
import com.main.tancol.common.dto.PageResponse;
import com.main.tancol.common.exeptions.IDNotFound;
import com.main.tancol.empleados.dto.request.DepartamentoRequestDTO;
import com.main.tancol.empleados.dto.response.DepartamentoResponseDTO;
import com.main.tancol.empleados.entities.DepartamentoEntity;
import com.main.tancol.empleados.repositories.DepartamentoRepository;
import com.main.tancol.empleados.service.interfaces.IDepartamentoService;
import com.main.tancol.empleados.util.DepartamentoMapper;
import com.main.tancol.empleados.util.DepartamentoPageSpecification;
import com.main.tancol.empleados.util.DepartamentoSpecification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DepartamentoService implements IDepartamentoService {
    private final DepartamentoRepository repository;

    public DepartamentoService(DepartamentoRepository departamentoRepository) {

        this.repository = departamentoRepository;
    }

    @Override
    public DepartamentoResponseDTO create(DepartamentoRequestDTO dto) {
        DepartamentoEntity departamento = DepartamentoMapper.DepartamentoRequesToToEntity(dto);
        return DepartamentoMapper.DepartamentoEntityToResponseDTO(this.repository.save(departamento));
    }

    @Override
    public List<DepartamentoResponseDTO> getAll(Map<String, String> params) {
        Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt");
        List<DepartamentoEntity> list = repository.findAll(filterWithParameters(params), sort);
        return list.stream().map(DepartamentoMapper::DepartamentoEntityToResponseDTO).toList();
    }

    private static final Set<String> PAGE_FILTERS = Set.of("nombre", "status", "createdAt", "updatedAt");
    private static final Map<String, String> PAGE_SORTS = Map.of(
            "nombre", "nombre",
            "status", "enabled",
            "createdAt", "createdAt",
            "updatedAt", "updatedAt"
    );

    @Override
    @Transactional(readOnly = true)
    public PageResponse<DepartamentoResponseDTO> getPage(Map<String, String> params) {
        CatalogPage query = CatalogPage.parse(params, PAGE_FILTERS, PAGE_SORTS.keySet());
        var result = repository.findAll(
                new DepartamentoPageSpecification(query.search(), query.filters()),
                query.pageable(query.sort(PAGE_SORTS, Sort.by(Sort.Direction.DESC, "createdAt"), "id"))
        );
        var items = result.getContent().stream().map(DepartamentoMapper::DepartamentoEntityToResponseDTO).toList();
        return new PageResponse<>(items, result.getTotalElements(), query.page(), query.pageSize());
    }

    @Override
    public DepartamentoResponseDTO getById(UUID id) {
        DepartamentoEntity query = this.repository.findById(id).orElseThrow( () -> new IDNotFound("No se encontro la id"));
        return DepartamentoMapper.DepartamentoEntityToResponseDTO(query);
    }

    @Override
    public DepartamentoResponseDTO update(UUID id, DepartamentoRequestDTO dto) {
        DepartamentoEntity query = this.repository.findById(id).orElseThrow( () -> new IDNotFound("No se encontro la id"));
        query.setNombre(dto.getNombre());
        query.setAlias(dto.getAlias());
        query.setDescripcion(dto.getDescripcion());
        query.setEnabled(dto.isEstado());
        return DepartamentoMapper.DepartamentoEntityToResponseDTO(this.repository.save(query));
    }

    @Override
    public DepartamentoResponseDTO delete(UUID dto) {
        DepartamentoEntity query = this.repository.findById(dto).orElseThrow( () -> new IDNotFound("No se encontro la id"));
        query.setDeleted(true);
        return DepartamentoMapper.DepartamentoEntityToResponseDTO(this.repository.save(query));
    }

    public Specification<DepartamentoEntity> filterWithParameters(Map<String, String> params) {
        return new DepartamentoSpecification().getSpecificationByFilters(params);
    }

}
