package com.main.tancol.producto.dto.request.comercial;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Value;

import java.math.BigDecimal;
import java.util.UUID;

@Value
@Getter
public class ProductoComercialRequestDTO {
    @NotNull(message = "Debe tener un nombre")
    String nombre;
    String descripcion;
    String sku;
    String alias;
    @NotNull(message = "Debe pasar un UUID Valido")
    UUID idCategoriaComercial;
    String idImagen;
    BigDecimal precio;
    UUID unidadMedidaDefault;
    Long presentacion;
    boolean enabled;
    // Agrega campos aqu??
}
