package com.main.tancol.almacen.dto.response;

import com.main.tancol.producto.dto.response.insumos.ProductoBasicoSimpleResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Builder
public class InventarioResponseDTO {
    private UUID idInventario;
    private BigDecimal cantidad;
    private AlmacenSimpleResponseDTO almacen;
    private ProductoBasicoSimpleResponseDTO producto;
    private UnidadesMedidaResponseDTO unidadesMedida;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
