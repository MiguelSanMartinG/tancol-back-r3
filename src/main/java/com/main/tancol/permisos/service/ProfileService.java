package com.main.tancol.permisos.service;

import com.main.tancol.common.exeptions.IDNotFound;
import com.main.tancol.permisos.dto.request.ProfileRequestDTO;
import com.main.tancol.permisos.dto.response.ProfileResponseDTO;
import com.main.tancol.permisos.entities.ModuleEntity;
import com.main.tancol.permisos.entities.RoleEntity;
import com.main.tancol.permisos.repositories.ModuleRepository;
import com.main.tancol.permisos.repositories.ProfileRepository;
import com.main.tancol.permisos.repositories.RoleRepository;
import com.main.tancol.permisos.util.ProfileValueMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.jpa.domain.Specification;
import com.main.tancol.permisos.entities.ProfileEntity;
import com.main.tancol.permisos.util.ProfileSpecification;
import com.main.tancol.permisos.service.interfaces.IProfileService;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ProfileService implements IProfileService {

    private final ProfileRepository repository;
    private final RoleRepository roleRepository;
    private final ModuleRepository moduleRepository;


    @Override
    public ProfileResponseDTO create(ProfileRequestDTO dto) {
        ModuleEntity m = moduleRepository.findById(dto.getIdModulo()).orElseThrow(() -> new IDNotFound("No se encontro"));
        RoleEntity r = roleRepository.findById(dto.getIdRol()).orElseThrow(() -> new IDNotFound("No se encontro"));
        ProfileEntity p = ProfileValueMapper.ProfileRequestToEntity(dto, m, r);

        return ProfileValueMapper.ProfileEntityToResponse(repository.save(p));

    }

    @Override
    public ProfileResponseDTO getById(UUID id) {
        return ProfileValueMapper.ProfileEntityToResponse(repository.findById(id).orElseThrow(() -> new IDNotFound("No se encontro")));
    }

    @Override
    public ProfileResponseDTO update(UUID id, ProfileRequestDTO dto) {
        ModuleEntity m = moduleRepository.findById(dto.getIdModulo()).orElseThrow(() -> new IDNotFound("No se encontro"));
        RoleEntity r = roleRepository.findById(dto.getIdRol()).orElseThrow(() -> new IDNotFound("No se encontro"));
        ProfileEntity p = repository.findById(id).orElseThrow(() -> new IDNotFound("id"));

        p.setModule(m);
        p.setRole(r);
        p.setCanUpdate(dto.isCanUpdate());
        p.setCanDelete(dto.isCanDelete());
        p.setCanRead(dto.isCanRead());
        p.setCanWrite(dto.isCanCreate());

        return ProfileValueMapper.ProfileEntityToResponse(repository.save(p));
    }

    @Override
    public ProfileResponseDTO delete(UUID id) {
        return null;
    }

    @Override
    public List<ProfileResponseDTO> getAll(Map<String, String> params) {
        List<ProfileEntity> list = repository.findAll(filterWithParameters(params));
        return list.stream().map(ProfileValueMapper::ProfileEntityToResponse).toList();
    }

    public Specification<ProfileEntity> filterWithParameters(Map<String, String> params) {
        return new ProfileSpecification().getSpecificationByFilters(params);
    }
}
