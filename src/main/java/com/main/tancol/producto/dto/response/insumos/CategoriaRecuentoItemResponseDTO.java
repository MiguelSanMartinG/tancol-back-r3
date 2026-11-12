package com.main.tancol.producto.dto.response.insumos;

import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.UUID;

@Value
@Builder
public class CategoriaRecuentoItemResponseDTO {
    UUID idCategoria;
    String nombre;
    int numeroProductos;
    List<ExistenciaUnidadResponseDTO> existencia;
}
