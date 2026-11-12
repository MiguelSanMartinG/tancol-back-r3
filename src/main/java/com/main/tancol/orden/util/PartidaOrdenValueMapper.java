package com.main.tancol.orden.util;

import com.main.tancol.almacen.entities.UnidadesMedidaEntity;
import com.main.tancol.almacen.util.UnidadesMedidaValueMapper;
import com.main.tancol.empleados.util.DepartamentoMapper;
import com.main.tancol.orden.dto.request.PartidaOrdenRequestDTO;
import com.main.tancol.orden.dto.response.PartidaOrdenResponseDTO;
import com.main.tancol.orden.entities.OrdenEntity;
import com.main.tancol.orden.entities.PartidaOrdenEntity;
import com.main.tancol.producto.entities.PresentacionProductoBasico;
import com.main.tancol.producto.entities.insumos.ProductoBasicoEntity;
import com.main.tancol.producto.util.insumos.ProductoValueMapper;
import com.main.tancol.proveedor.entity.TipoImpuestoEntity;
import com.main.tancol.proveedor.util.ProveedorValueMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Optional;

@Slf4j
public class PartidaOrdenValueMapper {

    public static PartidaOrdenEntity SavePartidaOrden(PartidaOrdenEntity dto, OrdenEntity orden,
                                                      ProductoBasicoEntity productoBasicoEntity, PresentacionProductoBasico presentacionProductoBasico,
                                                      TipoImpuestoEntity impuestoEntity) {
        dto.setCantidad(dto.getCantidad());
        dto.setPrecioUnitario(presentacionProductoBasico.getPrecio());
        dto.setDescuentoAplicado(dto.getDescuentoAplicado());
        dto.setImpuestoAplicado(impuestoEntity.getPorcentaje());
        dto.setSolicitudCompraEntity(orden);
        dto.setProducto(productoBasicoEntity);
        dto.setImpuestoRelacion(impuestoEntity);
        dto.setPrecioRelacion(presentacionProductoBasico);
        return dto;
    }



    public static PartidaOrdenEntity PartidaOrdenRequestToEntity(PartidaOrdenRequestDTO dto, OrdenEntity orden,
                                                                 ProductoBasicoEntity productoBasicoEntity, PresentacionProductoBasico presentacionProductoBasico,
                                                                 TipoImpuestoEntity impuestoEntity, UnidadesMedidaEntity unidadesMedida) {

        return PartidaOrdenEntity.builder()
                .id(Optional.ofNullable(dto.getIdPartida()).orElse(null))
                .cantidad(dto.getCantidad())
                .precioUnitario(presentacionProductoBasico.getPrecio())
                .descuentoAplicado(dto.getDescuentoAplicado())
                .impuestoAplicado(impuestoEntity.getPorcentaje())
                .solicitudCompraEntity(orden)
                .producto(productoBasicoEntity)
                .unidadesMedida(unidadesMedida)
                .impuestoRelacion(impuestoEntity)
                .precioRelacion(presentacionProductoBasico)
                .verificado(false)
                .build();

    }

    public static PartidaOrdenResponseDTO PartidasOrdenEntityToResponseDTO(PartidaOrdenEntity dto) {

        return PartidaOrdenResponseDTO.builder()
                .idPartida(dto.getId())
                .cantidad(dto.getCantidad())
                .precioUnitario(dto.getPrecioUnitario())
                .descuentoAplicado(dto.getDescuentoAplicado())
                .unidadesRecibidas(dto.getUnidadesRecibidas())
                .impuestoAplicado(dto.getImpuestoAplicado())
                .precioProveedor((dto.getPrecioRelacion() == null) ? null : ProductoValueMapper.PrecioEntityToResponse(dto.getPrecioRelacion()))
                .tipoImpuesto((dto.getImpuestoAplicado() == null) ? null : ProveedorValueMapper.TipoImpuestoEntityToResponse(dto.getImpuestoRelacion()))
                .almacen((dto.getAlmacen() == null) ? null : DepartamentoMapper.EntityToAlmacenResponseDTO(dto.getAlmacen()))
                .producto((dto.getProducto() == null) ? null : ProductoValueMapper.ProductEntityToResponseDTO(dto.getProducto()))
                .unidadesRecibidas(dto.getUnidadesRecibidas())
                .comentarioPartida(dto.getComentario())
                .unidadMedida(ObjectUtils.isNotEmpty(dto.getUnidadesMedida())? UnidadesMedidaValueMapper.UnidadesMedidaEntityToResponse(dto.getUnidadesMedida()) : null)
                .verificado(Boolean.TRUE.equals(dto.getVerificado()))
                .build();
    }


}
