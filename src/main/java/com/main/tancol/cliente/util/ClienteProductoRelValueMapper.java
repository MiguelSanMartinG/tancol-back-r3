package com.main.tancol.cliente.util;


import com.main.tancol.cliente.dto.request.ClienteProductoRequestDTO;
import com.main.tancol.cliente.dto.response.ClienteProductoResponseDTO;
import com.main.tancol.cliente.entities.ClienteEntity;
import com.main.tancol.cliente.entities.ClienteProductoEntity;
import com.main.tancol.producto.entities.comerciales.ProductoComercialEntity;
import com.main.tancol.producto.util.comercial.ProductoComercialValueMapper;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Optional;

public class ClienteProductoRelValueMapper {

        public static ClienteProductoEntity ClienteProductoRequestToEntity(ClienteProductoRequestDTO dto,
                                                                              ProductoComercialEntity productoComercialEntity,
                                                                              ClienteEntity clienteEntity) {
        return ClienteProductoEntity.builder()
                .productoComercial(ObjectUtils.isEmpty(productoComercialEntity) ? null : productoComercialEntity)
                .cliente(ObjectUtils.isEmpty(clienteEntity) ? null : clienteEntity)
                .descuento(dto.getDescuento())
                .build();

    }

    public static ClienteProductoResponseDTO ClienteProductoEntityToResponse(ClienteProductoEntity dto){
        return  ClienteProductoResponseDTO.builder()
                .idPrecio(Optional.ofNullable(dto.getId()).orElse(null))
                .descuento(dto.getDescuento())
                .cliente(ClienteValueMapper.ClienteEntityToResponseBasico(dto.getCliente()))
                .producto(ProductoComercialValueMapper.ProductoComercialEntityToResponseBasico(dto.getProductoComercial()))
                .build();
    }


}
