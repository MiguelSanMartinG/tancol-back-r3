package com.main.tancol.liquidacion.util.totalizados;


import com.main.tancol.empleados.util.EmpleadosValueMapper;
import com.main.tancol.liquidacion.dto.request.LiquidacionTotalizadoEmpleadoRequestDTO;
import com.main.tancol.liquidacion.dto.response.LiquidacionTotalizadoEmpleadoResponseDTO;
import com.main.tancol.liquidacion.entities.LiquidacionTotalizadoEmpleadoEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;

import java.util.LinkedList;
import java.util.stream.Collectors;

@Slf4j
public class LiquidacionTotalizadoEmpleadoValueMapper {

        public static LiquidacionTotalizadoEmpleadoEntity  LiquidacionTotalizadoEmpleadoRequestToEntity( LiquidacionTotalizadoEmpleadoRequestDTO dto) {
        return LiquidacionTotalizadoEmpleadoEntity.builder()
                .build();

    }

    public static LiquidacionTotalizadoEmpleadoResponseDTO  LiquidacionTotalizadoEmpleadoEntityToResponse(LiquidacionTotalizadoEmpleadoEntity dto){
            if(dto==null) return null;
//            log.info(dto.getAyudantes().toString());

        return   LiquidacionTotalizadoEmpleadoResponseDTO.builder()
                .idLiquiEmpleado(dto.getId())
                .empleado(EmpleadosValueMapper.EmpleadosEntityToResponse(dto.getEmpleado()))
                .total(dto.getCantidad())
                .ayudantes(ObjectUtils.isNotEmpty(dto.getAyudantes())
                        ? dto.getAyudantes().stream().map(LiquidacionTotalizadoEmpleadoValueMapper::LiquidacionTotalizadoEmpleadoEntityToResponse).collect(Collectors.toList())
                        : new LinkedList<>())
                .build();
    }


}
