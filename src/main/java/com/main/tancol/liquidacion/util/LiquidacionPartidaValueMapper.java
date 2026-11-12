package com.main.tancol.liquidacion.util;


import com.main.tancol.cliente.util.ClienteValueMapper;
import com.main.tancol.liquidacion.dto.request.LiquidacionPartidaRequestDTO;
import com.main.tancol.liquidacion.dto.response.LiquidacionPartidaResponseDTO;
import com.main.tancol.liquidacion.entities.LiquidacionPartidaEntity;
import com.main.tancol.liquidacion.util.conceptos.ConceptosVentasValueMapper;
import com.main.tancol.producto.util.comercial.ProductoComercialValueMapper;

public class LiquidacionPartidaValueMapper {

        public static LiquidacionPartidaEntity LiquidacionPartidaRequestToEntity(LiquidacionPartidaRequestDTO dto) {
        return LiquidacionPartidaEntity.builder()
                .cantidad(dto.getCantidad())
                .precio(dto.getPrecio())
                .formaPago(dto.getFormaPago())
                .factura(dto.getFactura())
                .build();

    }

    public static LiquidacionPartidaResponseDTO LiquidacionPartidaEntityToResponse(LiquidacionPartidaEntity dto){
        return  LiquidacionPartidaResponseDTO.builder()
                .idPartida(dto.getIdPartida())
                .cantidad(dto.getCantidad())
                .precio(dto.getPrecio())
                .descuento(dto.getDescuento())
                .productoComercial(ProductoComercialValueMapper.ProductoComercialEntityToResponse(dto.getProductoComercial()))
                .cliente(ClienteValueMapper.ClienteEntityToResponse(dto.getCliente()))
                .conceptosVentas(ConceptosVentasValueMapper.ConceptosEntityToResponse(dto.getConceptosVentas()))
                .factura(dto.isFactura())
                .folio(dto.getFolio())
                .formaPago(dto.getFormaPago())
                .importe(dto.getImporte())
                .importeDescontado(dto.getImporteDescontado())
                .cancelada(dto.isCancelada())
                .motivoCancelacion(dto.getMotivoCancelacion())
                .usuarioCancelacion(dto.getUsuarioCancelacion())
                .fechaCancelacion(dto.getFechaCancelacion())
                .build();
    }


}
