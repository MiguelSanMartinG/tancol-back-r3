package com.main.tancol.producto.dto.request.insumo;

import com.main.tancol.producto.entities.insumos.ProductoBasicoEntity;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO for {@link ProductoBasicoEntity}
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
     String sku;
     String alias;
     @NotNull(message = "Debe pasar un UUID Valido")
     UUID categoriaProductoId;
     String idImagen;
     UUID unidadMedidaDefault;
     String marca;
     String modelo;
     @DecimalMin(value = "0", message = "El stock mínimo no puede ser negativo")
     BigDecimal stockMinimo;
     @DecimalMin(value = "0", message = "El stock máximo no puede ser negativo")
     BigDecimal stockMaximo;
     Boolean notificarStockMinimo;
     Boolean tieneVigencia;
     LocalDate fechaVigencia;

     @AssertTrue(message = "Si el insumo tiene vigencia debe indicar la fecha de vigencia; si no la tiene, no envíe la fecha")
     public boolean isFechaVigenciaValida() {
          if (!Boolean.TRUE.equals(tieneVigencia)) {
               return fechaVigencia == null;
          }
          return fechaVigencia != null;
     }
}