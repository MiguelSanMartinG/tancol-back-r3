package com.main.tancol.producto.dto.response.comercial;

import com.main.tancol.almacen.dto.response.UnidadesMedidaResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Builder
public class ProductoComercialResponseDTO {
    private UUID idProductoComercial;
    private CategoriaProductoComercialSimpleResponseDTO categoriaProductoComercial;
    private String nombre;
    private String alias;
    private String descripcion;
    private String idImagen;
    private BigDecimal precio;
    private String sku;
    private Long presentacion;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UnidadesMedidaResponseDTO unidadMedidaDefault;
    private boolean enabled;
    // Agrega m??s campos aqu??
}
