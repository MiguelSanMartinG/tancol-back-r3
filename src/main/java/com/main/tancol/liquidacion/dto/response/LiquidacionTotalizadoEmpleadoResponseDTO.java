package com.main.tancol.liquidacion.dto.response;

import com.main.tancol.empleados.dto.response.EmpleadosResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Builder
public class LiquidacionTotalizadoEmpleadoResponseDTO {
    private UUID idLiquiEmpleado;
    private EmpleadosResponseDTO empleado;
    private BigDecimal total;
    private List<LiquidacionTotalizadoEmpleadoResponseDTO> ayudantes;
    // Agrega más campos aqu??
}
