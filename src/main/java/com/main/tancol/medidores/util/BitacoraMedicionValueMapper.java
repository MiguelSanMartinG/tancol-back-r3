package com.main.tancol.medidores.util;


import com.main.tancol.medidores.dto.request.BitacoraMedicionRequestDTO;
import com.main.tancol.medidores.dto.response.BitacoraMedicionResponseDTO;
import com.main.tancol.medidores.entities.BitacoraMedicionEntity;

import java.time.LocalDate;

public class BitacoraMedicionValueMapper {

        public static BitacoraMedicionEntity BitacoraMedicionRequestToEntity(BitacoraMedicionRequestDTO dto) {
        return BitacoraMedicionEntity.builder()
                .build();

    }

    public static BitacoraMedicionResponseDTO BitacoraMedicionEntityToResponse(BitacoraMedicionEntity dto) {
        return BitacoraMedicionEntityToResponse(dto, MedidorEstadoContext.VACIO);
    }

    public static BitacoraMedicionResponseDTO BitacoraMedicionEntityToResponse(
            BitacoraMedicionEntity dto,
            MedidorEstadoContext estado
    ) {
        return BitacoraMedicionResponseDTO.builder()
                .id(dto.getId())
                .medicionInicial(dto.getMedidaInicial())
                .medicionFinal(dto.getMedidaActual())
                .consumo(dto.getConsumo())
                .liquidado(dto.getLiquidado())
                .quedadoEnPipa(dto.getQuedadoEnPipa())
                .litrosMinuto(dto.getLitrosMinuto())
                .diferencia(dto.getDiferencia())
                .medidoHoy(dto.getCreatedAt() != null && dto.getCreatedAt().toLocalDate().equals(LocalDate.now()))
                .medidor(dto.getMedidor() != null
                        ? MedidoresValueMapper.MedidoresEntityToResponse(dto.getMedidor(), estado)
                        : null)
                .comentario(dto.getComentario())
                .usuario(dto.getNombreUsuario())
                .cratedAt(dto.getCreatedAt())
                .build();
    }


}
