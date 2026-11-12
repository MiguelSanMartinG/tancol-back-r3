package com.main.tancol.permisos.service;

import com.main.tancol.common.exeptions.IDNotFound;
import com.main.tancol.empleados.repositories.EmpleadosRepository;
import com.main.tancol.permisos.dto.request.RoleRequestDTO;
import com.main.tancol.permisos.dto.response.RoleResponseDTO;
import com.main.tancol.permisos.entities.RoleEntity;
import com.main.tancol.permisos.repositories.ModuleRepository;
import com.main.tancol.permisos.repositories.RoleRepository;
import com.main.tancol.permisos.service.interfaces.IRoleService;
import com.main.tancol.permisos.util.RoleSpecification;
import com.main.tancol.permisos.util.RoleValueMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService implements IRoleService {

    private final RoleRepository repository;
    private final ModuleRepository moduleRepository;
    private final EmpleadosRepository empleadosRepository;
    private final RoleRepository roleRepository;


    @Override
    public RoleResponseDTO create(RoleRequestDTO dto) {

        RoleEntity roleEntity = RoleEntity.builder()
                .name(dto.getRoleName())
                .build();

        RoleEntity rol = repository.save(roleEntity);
        return RoleValueMapper.RoleEntityToResponse(rol);
    }

    @Override
    public RoleResponseDTO getById(Long id) {
        RoleEntity rol = roleRepository.findById(id).orElseThrow(()-> new IDNotFound("id no encntrada"));
        return RoleValueMapper.RoleEntityToResponse(rol);
    }

    @Override
    public RoleResponseDTO update(Long id, RoleRequestDTO dto) {
        RoleEntity rol = roleRepository.findById(id).orElseThrow(()-> new IDNotFound("id no encntrada"));
        rol.setName(dto.getRoleName());

        return RoleValueMapper.RoleEntityToResponse(roleRepository.save(rol));
    }

    @Override
    public RoleResponseDTO delete(Long id) {
        RoleEntity rol = roleRepository.findById(id).orElseThrow(()-> new IDNotFound("id no encntrada"));
        rol.setDeleted(true);
        return RoleValueMapper.RoleEntityToResponse(roleRepository.save(rol));
    }

    @Override
    public List<RoleResponseDTO> getAll(Map<String, String> params) {
        List<RoleEntity> list = repository.findAll(filterWithParameters(params));
        return list.stream().map(RoleValueMapper::RoleEntityToResponse).toList();
//        return null;
    }


    public Specification<RoleEntity> filterWithParameters(Map<String, String> params) {
        return new RoleSpecification().getSpecificationByFilters(params);
    }

//    public Object updatePermisos(Long id, RoleRequestDTO dto) {
//        RoleEntity rol = roleRepository.findById(id).orElseThrow(()-> new IDNotFound("id no encntrada"));;
//            Set<RoleModulePermission> roles  = dto.getPermissions().stream().map(permission -> {
//
//                RoleModulePermission rolMod = null;
//                if(roleModulePermissionRepository.existsByRole_IdAndModule_Name(id, String.valueOf(permission.getModule()))) {
//                    rolMod = roleModulePermissionRepository.findByRole_IdAndModule_Name(id, String.valueOf(permission.getModule()));
//                    rolMod.setCanDelete(permission.isCanDelete());
//                    rolMod.setCanWrite(permission.isCanWrite());
//                    rolMod.setCanRead(permission.isCanRead());
//                    rolMod.setCanEdit(permission.isCanEdit());
//                }else {
//                    rolMod = roleModulePermissionRepository.save(RoleModulePermission.builder()
//                            .role(rol)
//                            .module(moduleRepository.findByName(dto.getRoleName()))
//                            .canRead(permission.isCanRead()).canDelete(permission.isCanDelete()).canWrite(permission.isCanWrite())
//                            .build());
//                }
//                return rolMod;
//            }).collect(Collectors.toSet());
//            rol.getPermissions().clear();
//            rol.getPermissions().addAll(roles);
//
//            return roleRepository.save(rol);
//        }
    }

