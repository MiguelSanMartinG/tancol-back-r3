package com.main.tancol.tablascomisiones.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Builder
public class ProductoComisionDisponibleResponseDTO {
    private UUID idProducto;
    private String nombre;
    private String alias;
}
