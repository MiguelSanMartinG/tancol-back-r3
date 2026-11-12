package com.main.tancol.logistica.util;

import com.main.tancol.empleados.util.EmpleadosValueMapper;
import com.main.tancol.logistica.dto.response.MermaVentaTrayectoResponseDTO;
import com.main.tancol.logistica.entities.bitacora.MermaVentaTrayectoEntity;
import org.apache.commons.lang3.ObjectUtils;

public class MermaVentaTrayectoValueMapper {

    public static MermaVentaTrayectoResponseDTO toResponse(MermaVentaTrayectoEntity entity) {
        if (entity == null) {
            return null;
        }
        return MermaVentaTrayectoResponseDTO.builder()
                .idMerma(entity.getId())
                .empleadoResponsable(ObjectUtils.isNotEmpty(entity.getEmpleadoResponsable())
                        ? EmpleadosValueMapper.EmpleadosEntityToResponse(entity.getEmpleadoResponsable())
                        : null)
                .empleadoFirmante(ObjectUtils.isNotEmpty(entity.getEmpleadoFirmante())
                        ? EmpleadosValueMapper.EmpleadosEntityToResponse(entity.getEmpleadoFirmante())
                        : null)
                .cantidad(entity.getCantidad())
                .rotoPorMaquina(entity.isRotoPorMaquina())
                .idImagenFirma(entity.getIdImagenFirma())
                .build();
    }
}
