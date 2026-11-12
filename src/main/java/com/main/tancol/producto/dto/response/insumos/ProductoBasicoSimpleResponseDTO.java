package com.main.tancol.producto.dto.response.insumos;

import com.main.tancol.producto.entities.insumos.ProductoBasicoEntity;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for {@link ProductoBasicoEntity}
 */
@AllArgsConstructor
@Value
@Builder
public class ProductoBasicoSimpleResponseDTO implements Serializable {
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    boolean enabled;
    UUID idProducto;
    @NotBlank(message = "Debe tener un nombre")
    String nombre;
    String idImagen;
    String descripcion;
    String sku;
    String alias;
    CategoriaProductoSimpleResponseDTO categoria;
    String marca;
    String modelo;
    BigDecimal stockMinimo;
    BigDecimal stockMaximo;
    boolean notificarStockMinimo;
    boolean tieneVigencia;
    LocalDate fechaVigencia;

}