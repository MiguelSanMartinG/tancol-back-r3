package com.main.tancol.empleados.util;


import com.main.tancol.empleados.dto.request.EmpleadosRequestDTO;
import com.main.tancol.empleados.dto.response.EmpleadosBasicResponseDTO;
import com.main.tancol.empleados.dto.response.EmpleadosResponseDTO;
import com.main.tancol.empleados.entities.DepartamentoEntity;
import com.main.tancol.empleados.entities.EmpleadosEntity;
import com.main.tancol.empleados.entities.TiposEmpleadosEntity;
import com.main.tancol.permisos.util.RoleValueMapper;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Optional;

public class EmpleadosValueMapper {

        public static EmpleadosEntity EmpleadosRequestToEntity(EmpleadosRequestDTO dto, TiposEmpleadosEntity empleadosEntity, DepartamentoEntity departamentoEntity) {
        return EmpleadosEntity.builder()
                .nombre(dto.getNombre())
                .primerApellido(dto.getApellidoPaterno())
                .segundoApellido(dto.getApellidoMaterno())
                .tiposEmpleados(empleadosEntity)
                .departamento(departamentoEntity)
                .fechaNacimiento(Optional.ofNullable(dto.getFechaNacimiento()).orElse(null))
                .correo(Optional.ofNullable(dto.getCorreo()).orElse(null))
                .telefono(Optional.ofNullable(dto.getTelefono()).orElse(null))
                .nombreUsuario(Optional.ofNullable(dto.getCorreo()).orElse(null))
                .contra(Optional.ofNullable(dto.getPassword()).orElse(null))
                .build();

    }

    public static EmpleadosResponseDTO EmpleadosEntityToResponse(EmpleadosEntity dto){
            if(dto == null)return null;
        return  EmpleadosResponseDTO.builder()
                .idEmpleado(dto.getId())
                .nombreUsuario(dto.getNombreUsuario())
                .nombre(dto.getNombre())
                .role(ObjectUtils.isNotEmpty(dto.getRole())? RoleValueMapper.RoleEntityToResponse(dto.getRole()) : null)
                .apellidoMaterno(Optional.ofNullable(dto.getSegundoApellido()).orElse(null))
                .apellidoPaterno(Optional.ofNullable(dto.getPrimerApellido()).orElse(null))
                .telefono(Optional.ofNullable(dto.getTelefono()).orElse(null))
                .email(Optional.ofNullable(dto.getCorreo()).orElse(null))
                .fechaNacimiento(Optional.ofNullable(dto.getFechaNacimiento()).orElse(null))
                .enabled(dto.isEnabled())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .departamento(dto.getDepartamento() != null ? DepartamentoMapper.DepartamentoEntityToResponseSimpleDTO(dto.getDepartamento()): null)
                .tipoEmpleado(dto.getTiposEmpleados() != null ?TiposEmpleadosValueMapper.TiposEmpleadosEntityToResponseSimpleDTO(dto.getTiposEmpleados()): null)
                .build();
    }

    public static EmpleadosResponseDTO EmpleadosEntityToSimpleResponse(EmpleadosEntity dto){
        return  EmpleadosResponseDTO.builder()
                .idEmpleado(dto.getId())
                .nombreUsuario(dto.getNombreUsuario())
                .nombre(dto.getNombre())
                .apellidoMaterno(Optional.ofNullable(dto.getSegundoApellido()).orElse(null))
                .apellidoPaterno(Optional.ofNullable(dto.getPrimerApellido()).orElse(null))
                .telefono(Optional.ofNullable(dto.getTelefono()).orElse(null))
                .email(Optional.ofNullable(dto.getCorreo()).orElse(null))
                .fechaNacimiento(Optional.ofNullable(dto.getFechaNacimiento()).orElse(null))
                .enabled(dto.isEnabled())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .departamento(dto.getDepartamento() != null ? DepartamentoMapper.DepartamentoEntityToResponseSimpleDTO(dto.getDepartamento()): null)
                .tipoEmpleado(dto.getTiposEmpleados() != null ?TiposEmpleadosValueMapper.TiposEmpleadosEntityToResponseSimpleDTO(dto.getTiposEmpleados()): null)
                .build();
    }

    public static EmpleadosBasicResponseDTO EmpleadosEntityToBasicResponse(EmpleadosEntity dto){
        return  EmpleadosBasicResponseDTO.builder()
                .idEmpleado(dto.getId())
                .nombre(dto.getNombre())
                .nombreUsuario(dto.getNombreUsuario())
                .enabled(dto.isEnabled())
                .departamento(dto.getDepartamento() != null ? DepartamentoMapper.DepartamentoEntityToResponseSimpleDTO(dto.getDepartamento()): null)
                .tipoEmpleado(dto.getTiposEmpleados() != null ?TiposEmpleadosValueMapper.TiposEmpleadosEntityToResponseSimpleDTO(dto.getTiposEmpleados()): null)
                .build();
    }


}
