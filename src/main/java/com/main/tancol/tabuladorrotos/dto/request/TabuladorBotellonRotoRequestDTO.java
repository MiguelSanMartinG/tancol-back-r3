package com.main.tancol.tabuladorrotos.dto.request;

import com.main.tancol.tabuladorrotos.enums.PeriodoTabuladorRoto;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.math.BigDecimal;
import java.util.UUID;

@Value
public class TabuladorBotellonRotoRequestDTO {

    @NotNull(message = "El empleado es obligatorio")
    UUID idEmpleado;

    @NotNull(message = "El periodo es obligatorio")
    PeriodoTabuladorRoto periodo;

    @NotNull(message = "La tolerancia es obligatoria")
    @DecimalMin(value = "0.01", message = "La tolerancia debe ser mayor a 0")
    @Digits(integer = 17, fraction = 2, message = "La tolerancia admite hasta dos decimales")
    BigDecimal tolerancia;

    @NotNull(message = "El producto comercial es obligatorio")
    UUID idProducto;

    @NotNull(message = "La unidad de medida es obligatoria")
    UUID idUnidadMedida;
}
