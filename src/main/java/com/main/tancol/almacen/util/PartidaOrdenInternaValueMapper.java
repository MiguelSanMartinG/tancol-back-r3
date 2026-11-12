package com.main.tancol.almacen.util;


import com.main.tancol.almacen.dto.request.partidas.PartidaOrdenInternaRequestDTO;
import com.main.tancol.almacen.dto.response.partidas.PartidaOrdenInternaResponseDTO;
import com.main.tancol.almacen.entities.AlmacenEntity;
import com.main.tancol.almacen.entities.OrdenInternaEntity;
import com.main.tancol.almacen.entities.UnidadesMedidaEntity;
import com.main.tancol.almacen.entities.partidas.PartidaOrdenInternaEntity;
import com.main.tancol.producto.entities.insumos.ProductoBasicoEntity;
import com.main.tancol.producto.util.insumos.ProductoValueMapper;

public class PartidaOrdenInternaValueMapper {

        public static PartidaOrdenInternaEntity PartidaOrdenInternaRequestToEntity(
                PartidaOrdenInternaRequestDTO partida, OrdenInternaEntity ordenInterna, AlmacenEntity almacenEntity, UnidadesMedidaEntity unidadesMedidaEntity, ProductoBasicoEntity productoBasicoEntity) {
        return PartidaOrdenInternaEntity.builder()
                .ordenInternaEntity(ordenInterna)
                .almacenEntity(almacenEntity)
                .productoBasicoEntity(productoBasicoEntity)
                .unidadesMedidaEntity(unidadesMedidaEntity)
                .comentario(partida.getComentarioPartida())
                .cantidad(partida.getCantidad())
                .build();

    }

    public static PartidaOrdenInternaResponseDTO PartidaOrdenInternaEntityToResponse(PartidaOrdenInternaEntity dto) {
        return  PartidaOrdenInternaResponseDTO.builder()
                .idPartida(dto.getIdPartida())
                .almacen(AlmacenValueMapper.AlmacenEntityToSimpleResponse(dto.getAlmacenEntity()))
                .productoBasico(ProductoValueMapper.ProductEntityToSimpleResponse(dto.getProductoBasicoEntity()))
                .cantidadTranspasada(dto.getCantidad())
                .unidadMedida(dto.getUnidadesMedidaEntity() == null ? null : dto.getUnidadesMedidaEntity().getAlias())
                .comentarioPartida(dto.getComentario())
                .build();
    }

}
