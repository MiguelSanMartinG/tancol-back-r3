package com.main.tancol.producto.dto.response.insumos;

import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.UUID;

@Value
@Builder
public class CategoriaExistenciaResponseDTO {
    UUID idCategoria;
    String nombre;
    List<CategoriaExistenciaProductoResponseDTO> productos;
    List<ExistenciaUnidadResponseDTO> totales;
}
