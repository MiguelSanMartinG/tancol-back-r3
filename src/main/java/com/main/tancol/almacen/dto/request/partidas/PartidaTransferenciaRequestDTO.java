package com.main.tancol.almacen.dto.request.partidas;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Value
public class PartidaTransferenciaRequestDTO {
    @NotNull(message = "Debe tener un numero de partida dentro")
    Integer indice ;
    @NotNull(message = "Debe haber un almacen de origen")
    UUID idAlmacenOrigen;
    @NotNull(message = "Debe haber un almacen de destino")
    UUID idAlmacenDestino;
    @NotNull(message = "Debe haber un inventario")
    UUID idProducto;
    @NotNull(message = "La cantidad es requerida")
    @Positive(message = "La cantidad a transferir debe ser mayor a cero")
    BigDecimal cantidad;
    UUID unidades;
    String compentarios;

}
