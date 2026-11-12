package com.main.tancol.tablasamonestaciones.dto.response;

import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class ProductoAmonestacionDisponibleResponseDTO {
    UUID idProducto;
    String nombre;
    String alias;
}
