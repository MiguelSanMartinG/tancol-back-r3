package com.main.tancol.logistica.util;


import com.main.tancol.empleados.entities.EmpleadosEntity;
import com.main.tancol.logistica.dto.request.BitacoraVentaRequestDTO;
import com.main.tancol.logistica.dto.response.BitacoraVentaResponseDTO;
import com.main.tancol.logistica.entities.bitacora.BitacoraVentaRutaEntity;
import org.apache.commons.lang3.ObjectUtils;

import java.util.HashSet;
import java.util.stream.Collectors;

public class BitacoraVentaValueMapper {
    public static BitacoraVentaRutaEntity BitacoraVentaRequestToEntity(BitacoraVentaRequestDTO dto,
                                                                       EmpleadosEntity empleado, String folio) {
        return BitacoraVentaRutaEntity.builder()
                .folio(folio)
                .build();
    }

    public static BitacoraVentaResponseDTO BitacoraVentaEntityToResponse(BitacoraVentaRutaEntity dto) {

        return BitacoraVentaResponseDTO.builder()
                .idBitacora(dto.getIdBitacora())
                .fecha(dto.getFecha().atStartOfDay())
                .folio(dto.getFolio())
                .bitacoraPartidaVentas(
                        ObjectUtils.isNotEmpty(dto.getTrayectos())?
                        dto.getTrayectos().stream().map(TrayectoValueMapper::TrayectoEntityToResponse).collect(Collectors.toSet())
                        : new HashSet<>())
                .status(dto.getEstatusBitacoraVenta())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .build();
    }

}
