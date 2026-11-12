package com.main.tancol.dashboard.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopInsumoCompradoResponseDTO {
    private UUID idProducto;
    private String producto;
    private BigDecimal cantidad;
    private String unidad;
}
