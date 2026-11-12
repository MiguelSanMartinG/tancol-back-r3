package com.main.tancol.producto.dto.response.insumos;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class ExistenciaUnidadResponseDTO {
    String unidad;
    BigDecimal cantidad;
}
