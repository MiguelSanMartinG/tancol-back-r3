package com.main.tancol.liquidacion.dto.request;

import lombok.Value;

import java.util.List;
import java.util.UUID;

@Value
public class LiquidacionTotalizadoRequestDTO {
    // Agrega campos aqu??
    UUID idProducto;
    UUID idTotalizado;
    List<LiquidacionTotalizadoEmpleadoRequestDTO> choferes;
}
