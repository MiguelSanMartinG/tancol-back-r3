package com.main.tancol.permisos.util;


import com.main.tancol.permisos.dto.request.*;
import com.main.tancol.permisos.dto.response.*;
import com.main.tancol.permisos.entities.*;
import org.apache.commons.lang3.ObjectUtils;

import java.util.List;

public class ProfileValueMapper {

        public static ProfileEntity ProfileRequestToEntity(ProfileRequestDTO dto, ModuleEntity module, RoleEntity role) {
        return ProfileEntity.builder()
                .role(ObjectUtils.isEmpty(role) ? null : role)
                .module(ObjectUtils.isEmpty(module) ? null : module)
                .canRead(dto.isCanRead())
                .canDelete(dto.isCanDelete())
                .canWrite(dto.isCanCreate())
                .canUpdate(dto.isCanUpdate())
                .build();

    }

    public static ProfileResponseDTO ProfileEntityToResponse(ProfileEntity dto){
        return  ProfileResponseDTO.builder()
                .idPerfil(dto.getIdPerfil())
                .module(ObjectUtils.isEmpty(dto.getModule())?null:ModuleValueMapper.ModuleEntityToResponse(dto.getModule()))
                .role(ObjectUtils.isEmpty(dto.getRole())?null:RoleValueMapper.RoleEntityToResponse(dto.getRole()))
                .canRead(dto.isCanRead())
                .canDelete(dto.isCanDelete())
                .canWrite(dto.isCanWrite())
                .canUpdate(dto.isCanUpdate())
                .build();
    }


}
