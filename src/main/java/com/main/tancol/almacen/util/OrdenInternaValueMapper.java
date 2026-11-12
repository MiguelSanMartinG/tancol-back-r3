package com.main.tancol.almacen.util;


import com.main.tancol.almacen.dto.request.*;
import com.main.tancol.almacen.dto.request.partidas.PartidaOrdenInternaRequestDTO;
import com.main.tancol.almacen.dto.response.*;
import com.main.tancol.almacen.dto.response.partidas.PartidaOrdenInternaResponseDTO;
import com.main.tancol.almacen.entities.*;
import com.main.tancol.almacen.entities.partidas.PartidaOrdenInternaEntity;
import com.main.tancol.empleados.entities.EmpleadosEntity;
import com.main.tancol.empleados.util.EmpleadosValueMapper;

import java.util.ArrayList;
import java.util.List;

public class OrdenInternaValueMapper {

        public static OrdenInternaEntity OrdenInternaRequestToEntity(OrdenInternaRequestDTO dto, EmpleadosEntity empleadoEmisor,
                                                                     EmpleadosEntity empleadoRecepcion) {
        return OrdenInternaEntity.builder()
                .uso(dto.getUso())
                .empleadoEnvia(empleadoEmisor)
                .empleadoRecibe(empleadoRecepcion)
                .comentarios(dto.getComentarios())
                .estadoOrdenInterna(dto.getEstado())
                .fechaElaboracion(dto.getFechaElaboracion())
//                .partidas(dto.getPartidas().stream().map((x) -> PartidaOrdenInternaValueMapper.PartidaOrdenInternaRequestToEntity(x)).toList())
                .partidas(new ArrayList<>())
                .build();

    }

    public static OrdenInternaResponseDTO OrdenInternaEntityToResponse(OrdenInternaEntity dto){
        return  OrdenInternaResponseDTO.builder()
                .idOrdenInterna(dto.getIdOrdenInterna())
                .uso(dto.getUso())
                .folio(dto.getFolio())
                .empleadoEmisor(EmpleadosValueMapper.EmpleadosEntityToResponse(dto.getEmpleadoEnvia()))
                .empleadoReceptor(EmpleadosValueMapper.EmpleadosEntityToResponse(dto.getEmpleadoRecibe()))
                .comentario(dto.getComentarios())
                .partidas(dto.getPartidas().stream().map((x) -> PartidaOrdenInternaValueMapper.PartidaOrdenInternaEntityToResponse(x)).toList())
                .estado(dto.getEstadoOrdenInterna())
                .fechaElaboracion(dto.getFechaElaboracion())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .build();
    }


}
