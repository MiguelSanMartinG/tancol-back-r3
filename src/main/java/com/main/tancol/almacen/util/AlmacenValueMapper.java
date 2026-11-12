package com.main.tancol.almacen.util;

import com.main.tancol.almacen.dto.request.AlmacenRequestDTO;
import com.main.tancol.almacen.dto.response.AlmacenSimpleResponseDTO;
import com.main.tancol.almacen.entities.AlmacenEntity;

public class AlmacenValueMapper {



        public static AlmacenEntity AlmacenRequestToEntity(AlmacenRequestDTO dto) {
            return null;

        }

        public static AlmacenSimpleResponseDTO AlmacenEntityToSimpleResponse(AlmacenEntity dto){
            return  AlmacenSimpleResponseDTO.builder()
                    .idAlmacen(dto.getId())
                    .enabled(dto.isEnabled())
                    .nombre(dto.getNombre())
                    .build();
        }

}
