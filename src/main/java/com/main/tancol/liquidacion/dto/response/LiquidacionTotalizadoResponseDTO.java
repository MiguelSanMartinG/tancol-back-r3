package com.main.tancol.liquidacion.dto.response;

import com.main.tancol.producto.dto.response.comercial.ProductoComercialResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Builder
public class LiquidacionTotalizadoResponseDTO {
    private UUID id;
    private ProductoComercialResponseDTO producto;
    private List<LiquidacionTotalizadoEmpleadoResponseDTO> choferes;
    // Agrega m??s campos aqu??
}
