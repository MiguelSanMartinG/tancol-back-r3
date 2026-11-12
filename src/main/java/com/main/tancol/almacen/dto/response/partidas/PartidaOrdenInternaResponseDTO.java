package com.main.tancol.almacen.dto.response.partidas;

import com.main.tancol.almacen.dto.response.AlmacenSimpleResponseDTO;
import com.main.tancol.producto.dto.response.insumos.ProductoBasicoSimpleResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Builder
public class PartidaOrdenInternaResponseDTO {
    private UUID idPartida;
    private AlmacenSimpleResponseDTO almacen;
    private ProductoBasicoSimpleResponseDTO productoBasico;
    private BigDecimal cantidadTranspasada;
    private String unidadMedida;
    private String comentarioPartida;
}
