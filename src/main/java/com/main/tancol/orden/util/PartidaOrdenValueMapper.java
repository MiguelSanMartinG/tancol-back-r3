package com.main.tancol.orden.util;

import com.main.tancol.almacen.util.ValueMapper;
import com.main.tancol.orden.dto.request.PartidaOrdenRequestDTO;
import com.main.tancol.orden.dto.response.PartidaOrdenResponseDTO;
import com.main.tancol.orden.entities.OrdenEntity;
import com.main.tancol.orden.entities.PartidaOrdenEntity;
import com.main.tancol.producto.entities.ProductoEntity;
import com.main.tancol.proveedor.entity.TipoImpuesto;

import java.math.BigDecimal;

public class PartidaOrdenValueMapper {
    public static PartidaOrdenEntity PartidaOrdenRequestToEntity(PartidaOrdenRequestDTO dto, OrdenEntity orden, TipoImpuesto tipoImpuesto, ProductoEntity productoEntity) {

            return PartidaOrdenEntity.builder()
                .cantidad(dto.getCantidad())
                .precioUnitario(dto.getPrecioUnitario())
                .solicitudCompraEntity(orden)
                .iva(BigDecimal.valueOf(tipoImpuesto.getPorcentaje()))
                .productoEntity(productoEntity)
                .build();

    }

    public static PartidaOrdenResponseDTO PartidasOrdenEntityToResponseDTO(PartidaOrdenEntity dto) {
        return PartidaOrdenResponseDTO.builder()
                .id(dto.getId())
                .cantidad(dto.getCantidad())
                .precio(dto.getPrecioUnitario())
                .unidadesRecibidas(dto.getUnidadesRecibidas())
                .comentariosRecibo(dto.getComentario())
                .almacen((dto.getAlmacen() == null) ?  null : ValueMapper.EntityToAlmacenResponseDTO(dto.getAlmacen()))
                .build();
    }

//
//    public static PartidaCompraResponseDTO PartidaCompraEntityToResponse(PartidaCompraEntity dto){
//        return  PartidaCompraResponseDTO.builder()
//                .build();
//    }


}
