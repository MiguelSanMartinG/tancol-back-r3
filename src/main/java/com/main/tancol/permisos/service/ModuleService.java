package com.main.tancol.permisos.service;

import com.main.tancol.common.exeptions.IDNotFound;
import com.main.tancol.permisos.dto.request.ModuleRequestDTO;
import com.main.tancol.permisos.dto.response.ModuleResponseDTO;
import com.main.tancol.permisos.entities.ModuleEntity;
import com.main.tancol.permisos.repositories.ModuleRepository;
import com.main.tancol.permisos.service.interfaces.IModuleService;
import com.main.tancol.permisos.util.ModuleSpecification;
import com.main.tancol.permisos.util.ModuleValueMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ModuleService implements IModuleService {

    private final ModuleRepository repository;


    @Override
    public ModuleResponseDTO create(ModuleRequestDTO dto) {

        ModuleEntity parent = null;
        if(dto.getParentId() != null){
            parent = repository.findById(dto.getParentId()).orElseThrow(() -> new IDNotFound("El modulo padre no funciona"));
        }

        ModuleEntity moduleEntity = ModuleEntity.builder()
                .name(dto.getName())
                .rutaModulo(dto.getRutaModulo())
                .parent(parent).build();
        return ModuleValueMapper.ModuleEntityToResponse(repository.save(moduleEntity));
    }

    @Override
    public ModuleResponseDTO getById(UUID id) {
         ModuleEntity m = repository.findById(id).orElseThrow(() -> new IDNotFound("No se encontro el modulo especificado."));
         return ModuleValueMapper.ModuleEntityToResponse(m);
    }

    @Override
    public ModuleResponseDTO update(UUID id, ModuleRequestDTO dto) {
        ModuleEntity m = repository.findById(id).orElseThrow(() -> new IDNotFound("No se encontro el modulo especificado."));
        ModuleEntity parent = null;

        if(dto.getParentId() != null){
            parent = repository.findById(dto.getParentId()).orElseThrow(() -> new IDNotFound("El modulo padre no funciona"));
        }
        m.setName(dto.getName());
        m.setParent(parent);
        m.setRutaModulo(dto.getRutaModulo());
        return ModuleValueMapper.ModuleEntityToResponse(this.repository.save(m));
    }

    @Override
    public ModuleResponseDTO delete(UUID id) {
        ModuleEntity m = repository.findById(id).orElseThrow(() -> new IDNotFound("No se encontro el modulo especificado."));
        m.setDeleted(true);
        return ModuleValueMapper.ModuleEntityToResponse(repository.save(m));
    }

    @Override
    public List<ModuleResponseDTO> getAll(Map<String, String> params) {
        List<ModuleEntity> list = repository.findAll(filterWithParameters(params));
        return list.stream().map(ModuleValueMapper::ModuleEntityToResponse).toList();
    }

    public Specification<ModuleEntity> filterWithParameters(Map<String, String> params) {
        return new ModuleSpecification().getSpecificationByFilters(params);
    }
}
