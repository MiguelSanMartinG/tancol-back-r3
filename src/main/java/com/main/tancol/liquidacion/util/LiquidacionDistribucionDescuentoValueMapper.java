package com.main.tancol.liquidacion.util;


import com.main.tancol.liquidacion.dto.request.LiquidacionDistribucionDescuentoRequestDTO;
import com.main.tancol.liquidacion.dto.response.LiquidacionDistribucionDescuentoResponseDTO;
import com.main.tancol.liquidacion.entities.LiquidacionDistribucionDescuentoEntity;


public class LiquidacionDistribucionDescuentoValueMapper {

        public static LiquidacionDistribucionDescuentoEntity LiquidacionDistribucionDescuentoRequestToEntity(LiquidacionDistribucionDescuentoRequestDTO dto) {
        return LiquidacionDistribucionDescuentoEntity.builder()
                .build();

    }

    public static LiquidacionDistribucionDescuentoResponseDTO LiquidacionDistribucionDescuentoEntityToResponse(LiquidacionDistribucionDescuentoEntity dto){



            return  LiquidacionDistribucionDescuentoResponseDTO.builder()
                .id(dto.getId())
                .idLiquidacionPartida(dto.getLiquidacionPartidaEntity().getIdPartida())
                .descuento(dto.getLiquidacionPartidaEntity().getDescuento())
                .totalDescuento(dto.getLiquidacionPartidaEntity().getDescuento().multiply(dto.getLiquidacionPartidaEntity().getCantidad()))
                .tipo(dto.getLiquidacionPartidaEntity().getConceptosVentas().getConcepto())
                .cuentaA(dto.getCuentaA())
                .cuentaB(dto.getCuentaB())
                .cuentaC(dto.getCuentaC())
                .build();
    }


}
