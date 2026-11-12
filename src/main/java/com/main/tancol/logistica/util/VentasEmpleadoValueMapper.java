package com.main.tancol.logistica.util;


import com.main.tancol.empleados.util.EmpleadosValueMapper;
import com.main.tancol.logistica.dto.request.partidas.VentasEmpleadoRequestDTO;
import com.main.tancol.logistica.dto.response.VentasEmpleadoResponseDTO;
import com.main.tancol.logistica.entities.bitacora.comisiones.VentasEmpleadoEntity;
import org.apache.commons.lang3.ObjectUtils;

public class VentasEmpleadoValueMapper {

        public static VentasEmpleadoEntity VentasEmpleadoRequestToEntity(VentasEmpleadoRequestDTO dto) {
        return VentasEmpleadoEntity.builder()
                .build();

    }

    public static VentasEmpleadoResponseDTO VentasEmpleadoEntityToResponse(VentasEmpleadoEntity dto){
        return  VentasEmpleadoResponseDTO.builder()
                .comision(dto.getComision())
                .empleado(ObjectUtils.isNotEmpty(dto.getEmpleado()) ? EmpleadosValueMapper.EmpleadosEntityToResponse(dto.getEmpleado()) : null)
                .build();
    }


}
