package com.main.tancol.tablasamonestaciones.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.math.BigDecimal;
import java.util.UUID;

@Value
public class TablasAmonestacionesRequestDTO {

    @NotNull(message = "De es obligatorio")
    @Min(value = 1, message = "De debe ser un entero mayor o igual a 1")
    Integer cantidadDesde;

    @NotNull(message = "A es obligatorio")
    @Min(value = 1, message = "A debe ser un entero mayor o igual a 1")
    Integer cantidadHasta;

    @NotNull(message = "El producto es obligatorio")
    UUID idProducto;

    @NotNull(message = "El descuento es obligatorio")
    @DecimalMax(value = "0.00", message = "El descuento no puede ser positivo")
    @Digits(integer = 17, fraction = 2, message = "El descuento admite hasta dos decimales")
    BigDecimal descuento;

    @AssertTrue(message = "A debe ser igual o mayor que De")
    public boolean isRangoValido() {
        if (cantidadDesde == null || cantidadHasta == null) {
            return true;
        }
        return cantidadHasta >= cantidadDesde;
    }
}
