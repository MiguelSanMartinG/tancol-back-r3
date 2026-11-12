package com.main.tancol.prestamo.util;

import com.main.tancol.cliente.dto.response.ClienteResponseBasicoDTO;
import com.main.tancol.cliente.entities.ClienteEntity;
import com.main.tancol.cliente.util.ClienteValueMapper;
import com.main.tancol.prestamo.dto.request.EquipoRequestDTO;
import com.main.tancol.prestamo.dto.response.EquipoResponseDTO;
import com.main.tancol.prestamo.entities.EquipoEntity;
import com.main.tancol.prestamo.entities.TipoEquipoEntity;
import com.main.tancol.prestamo.enums.EstadoEquipo;

public class EquipoValueMapper {

    public static EquipoEntity requestToEntity(EquipoRequestDTO dto, TipoEquipoEntity tipoEquipo) {
        return EquipoEntity.builder()
                .nombre(dto.getNombre())
                .sku(dto.getSku())
                .idImagen(dto.getIdImagen())
                .descripcion(dto.getDescripcion())
                .marca(dto.getMarca())
                .modelo(dto.getModelo())
                .tipoEquipo(tipoEquipo)
                .estado(EstadoEquipo.DISPONIBLE)
                .build();
    }

    public static EquipoResponseDTO entityToResponse(EquipoEntity entity) {
        return entityToResponse(entity, null);
    }

    public static EquipoResponseDTO entityToResponse(EquipoEntity entity, ClienteEntity clienteAsignado) {
        if (entity == null) {
            return null;
        }
        ClienteResponseBasicoDTO clienteDto = clienteAsignado != null
                ? ClienteValueMapper.ClienteEntityToResponseBasico(clienteAsignado)
                : null;
        return EquipoResponseDTO.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .sku(entity.getSku())
                .idImagen(entity.getIdImagen())
                .descripcion(entity.getDescripcion())
                .marca(entity.getMarca())
                .modelo(entity.getModelo())
                .estado(entity.getEstado())
                .tipoEquipo(TipoEquipoValueMapper.entityToSimpleResponse(entity.getTipoEquipo()))
                .clienteAsignado(clienteDto)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .enabled(entity.isEnabled())
                .build();
    }
}
