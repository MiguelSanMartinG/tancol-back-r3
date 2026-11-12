package com.main.tancol.almacen.dto.request.partidas;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Value
public class PartidaOrdenInternaRequestDTO {
    @NotNull(message = "Debe haber un almacen")
    UUID idAlmacen;
    @NotNull(message = "Debe haber un producto")
    UUID idProductoBasico;
    @NotNull(message = "La cantidad es requerida")
    @Positive(message = "La cantidad debe ser mayor a cero")
    BigDecimal cantidad;
    UUID unidadMedida;
    String comentarioPartida;
}
