package com.main.tancol.prestamo.util;

import com.main.tancol.cliente.entities.ClienteEntity;
import com.main.tancol.cliente.util.ClienteValueMapper;
import com.main.tancol.prestamo.dto.request.EquipoAsignacionRequestDTO;
import com.main.tancol.prestamo.dto.response.EquipoAsignacionResponseDTO;
import com.main.tancol.prestamo.entities.EquipoAsignacionEntity;
import com.main.tancol.prestamo.entities.EquipoEntity;
import com.main.tancol.prestamo.enums.PeriodoCoberturaEquipo;

import java.time.LocalDateTime;

public class EquipoAsignacionValueMapper {

    public static EquipoAsignacionEntity requestToEntity(EquipoAsignacionRequestDTO dto,
                                                        EquipoEntity equipo,
                                                        ClienteEntity cliente) {
        LocalDateTime fecha = dto.getFechaAsignacion() != null
                ? dto.getFechaAsignacion()
                : LocalDateTime.now();
        PeriodoCoberturaEquipo periodo = dto.getPeriodoCobertura() != null
                ? dto.getPeriodoCobertura()
                : PeriodoCoberturaEquipo.SEMANAL;
        return EquipoAsignacionEntity.builder()
                .equipo(equipo)
                .cliente(cliente)
                .fechaAsignacion(fecha)
                .montoMinimo(dto.getMontoMinimo())
                .periodoCobertura(periodo)
                .activa(true)
                .build();
    }

    public static EquipoAsignacionResponseDTO entityToResponse(EquipoAsignacionEntity entity) {
        if (entity == null) {
            return null;
        }
        return EquipoAsignacionResponseDTO.builder()
                .id(entity.getId())
                .equipo(EquipoValueMapper.entityToResponse(entity.getEquipo(), entity.getCliente()))
                .cliente(ClienteValueMapper.ClienteEntityToResponseBasico(entity.getCliente()))
                .fechaAsignacion(entity.getFechaAsignacion())
                .fechaDevolucion(entity.getFechaDevolucion())
                .montoMinimo(entity.getMontoMinimo())
                .periodoCobertura(entity.getPeriodoCobertura())
                .activa(entity.isActiva())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .enabled(entity.isEnabled())
                .build();
    }
}
