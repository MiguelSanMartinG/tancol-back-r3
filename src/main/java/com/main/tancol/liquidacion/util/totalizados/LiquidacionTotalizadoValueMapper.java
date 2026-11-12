package com.main.tancol.liquidacion.util.totalizados;


import com.main.tancol.liquidacion.dto.request.LiquidacionTotalizadoRequestDTO;
import com.main.tancol.liquidacion.dto.response.LiquidacionTotalizadoResponseDTO;
import com.main.tancol.liquidacion.entities.LiquidacionTotalizadoEntity;
import com.main.tancol.producto.util.comercial.ProductoComercialValueMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.stream.Collectors;

@Slf4j
public class LiquidacionTotalizadoValueMapper {

        public static LiquidacionTotalizadoEntity TotalizadoChoferesRequestToEntity(LiquidacionTotalizadoRequestDTO dto) {
        return LiquidacionTotalizadoEntity.builder()
                .build();

    }

    public static LiquidacionTotalizadoResponseDTO LiquidacionTotalizadoEntityToResponse(LiquidacionTotalizadoEntity dto){
            if(dto == null){
                return null;
            }
//            for (LiquidacionTotalizadoEmpleadoEntity x:dto.getEmpleadoTotalizadoLiquidacion()){
//                log.info(x.getEmpleado().getNombre());
//            }
        return  LiquidacionTotalizadoResponseDTO.builder()
                .id(dto.getId())
                .producto(ProductoComercialValueMapper.ProductoComercialEntityToResponse(dto.getProductoComercial()))
                .choferes(dto.getEmpleadoTotalizadoLiquidacion()
                        .stream()
//                        .filter(x -> ObjectUtils.isNotEmpty(x.getChofer()))
//                        .peek(n -> log.info("Nombre Chofer: {}", n.getEmpleado().getNombre()))
//                        .peek(n -> log.info("Es chofer: {}" ,n.getChofer()))
                        .filter(x -> x.getChofer())
                        .map(LiquidacionTotalizadoEmpleadoValueMapper::LiquidacionTotalizadoEmpleadoEntityToResponse)
                        .collect(Collectors.toList()))
                .build();
    }


}
