package com.main.tancol.almacen.util;


import com.main.tancol.almacen.dto.request.partidas.PartidaTransferenciaRequestDTO;
import com.main.tancol.almacen.dto.response.partidas.PartidaTransferenciaResponseDTO;
import com.main.tancol.almacen.entities.*;
import com.main.tancol.almacen.entities.partidas.PartidaTransferenciaEntity;
import com.main.tancol.empleados.entities.EmpleadosEntity;
import com.main.tancol.producto.entities.insumos.ProductoBasicoEntity;
import com.main.tancol.producto.util.insumos.ProductoValueMapper;
import org.apache.commons.lang3.ObjectUtils;

public class PartidaTransferenciaValueMapper {

        public static PartidaTransferenciaEntity PartidaTransferenciaRequestToEntity(PartidaTransferenciaRequestDTO dto, AlmacenEntity almacenOrigen, AlmacenEntity almacenDestino, ProductoBasicoEntity productoBasicoEntity, UnidadesMedidaEntity uMedida, EmpleadosEntity empleados) {
        return PartidaTransferenciaEntity.builder()
                .comentarios(dto.getCompentarios())
                .almacenOrigen(almacenOrigen)
                .almacenDestino(almacenDestino)
                .productoBasicoEntity(productoBasicoEntity)
                .unidadesMedidaEntity(uMedida)
                .cantidades(dto.getCantidad())
                .empleadosEntity(empleados)
                .build();

    }

    public static PartidaTransferenciaResponseDTO PartidaTransferenciaEntityToResponse(PartidaTransferenciaEntity dto){
        return  PartidaTransferenciaResponseDTO.builder()
                .idPartidaTransferencia(dto.getIdPartida())
                .idAlmacenDestino(dto.getAlmacenDestino().getId())
                .nombreAlmacenDestino(dto.getAlmacenDestino().getNombre())
                .idAlmacenOrigen(dto.getAlmacenOrigen().getId())
                .nombreAlmacenOrigen(dto.getAlmacenOrigen().getNombre())
                .fechaDeTransferencia(dto.getCreatedAt())
                .cantidadTrasferida(dto.getCantidades())
                .unidadesMedida(ObjectUtils.isEmpty(dto.getUnidadesMedidaEntity())? null : UnidadesMedidaValueMapper.UnidadesMedidaEntityToResponse(dto.getUnidadesMedidaEntity()))
                .producto(ProductoValueMapper.ProductEntityToSimpleResponse(dto.getProductoBasicoEntity()))
                .nombreUsuario(dto.getEmpleadosEntity() == null ? null : dto.getEmpleadosEntity().getNombreUsuario())
                .comentario(dto.getComentarios())
                .build();
    }


}
