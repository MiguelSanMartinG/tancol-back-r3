package com.main.tancol.producto.dto.response.insumos;

import com.main.tancol.almacen.dto.response.UnidadesMedidaResponseDTO;
import com.main.tancol.producto.entities.insumos.ProductoBasicoEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
@Value
@Builder
public class ProductoResponseDTO implements Serializable {
    UUID idProducto;
    @NotNull(message = "Debe tener un nombre")
    String nombre;
    String alias;
    @NotBlank
    String descripcion;
    @NotBlank
    String sku;
    String nombreCategoria;
    UUID idCategoriaPadre;
    String nombreCategoriaPadre;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    String idImagen;
    Boolean enabled;
    UnidadesMedidaResponseDTO unidadMedidaDefault;
    String marca;
    String modelo;
    BigDecimal stockMinimo;
    BigDecimal stockMaximo;
    boolean notificarStockMinimo;
    boolean tieneVigencia;
    LocalDate fechaVigencia;
}