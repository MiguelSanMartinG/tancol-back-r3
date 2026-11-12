package com.main.tancol.almacen.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO for {@link com.main.tancol.almacen.entities.InventarioEntity}
 */
@Value
public class InventarioRequestDTO {

    @NotNull
    UUID idAlmacen;
    @NotNull
    UUID idProducto;
    @NotNull(message = "La cantidad es requerida")
    @DecimalMin(value = "0.0", message = "La cantidad no puede ser negativa")
    BigDecimal cantidad;
    UUID unidadDeMedida;
    BigDecimal precioProveedor;
    UUID idProveedor;

}
