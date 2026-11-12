package com.main.tancol.orden.dto.response;

import com.main.tancol.almacen.dto.response.AlmacenResponseDTO;
import com.main.tancol.almacen.dto.response.UnidadesMedidaResponseDTO;
import com.main.tancol.producto.dto.response.PrecioResponseDTO;
import com.main.tancol.producto.dto.response.insumos.ProductoResponseDTO;
import com.main.tancol.proveedor.dto.response.TipoImpuestoResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Builder
public class PartidaOrdenResponseDTO {

    private UUID idPartida;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal descuentoAplicado;
    private BigDecimal impuestoAplicado;

    private PrecioResponseDTO precioProveedor;
    private TipoImpuestoResponseDTO tipoImpuesto;
    private AlmacenResponseDTO almacen;
    private ProductoResponseDTO producto;

    //RECIBO
    private Integer unidadesRecibidas;
    private UnidadesMedidaResponseDTO unidadMedida;
    private String comentarioPartida;
    private boolean verificado;
}
