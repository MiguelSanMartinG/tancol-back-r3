package com.main.tancol.logistica.dto.response;

import com.main.tancol.empleados.dto.response.EmpleadosResponseDTO;
import com.main.tancol.logistica.entities.bitacora.comisiones.VentasEmpleadoEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.io.Serializable;
import java.math.BigDecimal;

@AllArgsConstructor
@Builder
/**
 * DTO for {@link VentasEmpleadoEntity}
 */
@Value
public class VentasEmpleadoResponseDTO implements Serializable {
    EmpleadosResponseDTO empleado;
    BigDecimal comision;
}
