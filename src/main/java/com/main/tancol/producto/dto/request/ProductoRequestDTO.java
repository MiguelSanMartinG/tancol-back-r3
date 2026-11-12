package com.main.tancol.producto.dto.request;

import com.main.tancol.producto.entities.ProductoEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

/**
 * DTO for {@link ProductoEntity}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class ProductoRequestDTO implements Serializable {
     @NotNull(message = "Debe tener un nombre")
     String nombre;
     String descripcion;
     @NotBlank(message = "El SKU debe ser introducido.")
     String sku;
     String alias;
     @NotNull(message = "Debe pasar un UUID Valido")
     UUID categoriaProductoId;
     Boolean activo;
}