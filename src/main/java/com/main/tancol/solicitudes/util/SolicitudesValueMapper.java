package com.main.tancol.solicitudes.util;


import com.main.tancol.solicitudes.dto.request.SolicitudesRequestDTO;
import com.main.tancol.solicitudes.dto.response.SolicitudesResponseDTO;
import com.main.tancol.solicitudes.entities.SolicitudEntity;

public class SolicitudesValueMapper {

        public static SolicitudEntity SolicitudesRequestToEntity(SolicitudesRequestDTO dto) {
        return SolicitudEntity.builder()
                .build();

    }

    public static SolicitudesResponseDTO SolicitudesEntityToResponse(SolicitudEntity dto){
        return  SolicitudesResponseDTO.builder()
                .build();
    }


}
