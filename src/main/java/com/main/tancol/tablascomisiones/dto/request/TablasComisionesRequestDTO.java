package com.main.tancol.tablascomisiones.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.math.BigDecimal;
import java.util.UUID;

@Value
public class TablasComisionesRequestDTO {

    @NotNull(message = "La venta mínima es obligatoria")
    @Min(value = 1, message = "La venta mínima debe ser un entero mayor o igual a 1")
    Integer ventaMinima;

    @NotNull(message = "De es obligatorio")
    @Min(value = 1, message = "De debe ser un entero mayor o igual a 1")
    Integer cantidadDesde;

    @NotNull(message = "A es obligatorio")
    @Min(value = 1, message = "A debe ser un entero mayor o igual a 1")
    Integer cantidadHasta;

    @NotNull(message = "El producto es obligatorio")
    UUID idProducto;

    @NotNull(message = "La comisión es obligatoria")
    @DecimalMin(value = "0.00", message = "La comisión no puede ser negativa")
    @Digits(integer = 17, fraction = 2, message = "La comisión admite hasta dos decimales")
    BigDecimal comision;

    @AssertTrue(message = "A debe ser igual o mayor que De")
    public boolean isRangoValido() {
        if (cantidadDesde == null || cantidadHasta == null) {
            return true;
        }
        return cantidadHasta >= cantidadDesde;
    }
}
