package com.main.tancol.logistica.dto.response;

import com.main.tancol.empleados.dto.response.EmpleadosResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Value
@Builder
@AllArgsConstructor
public class MermaVentaTrayectoResponseDTO implements Serializable {
    UUID idMerma;
    EmpleadosResponseDTO empleadoResponsable;
    EmpleadosResponseDTO empleadoFirmante;
    BigDecimal cantidad;
    boolean rotoPorMaquina;
    String idImagenFirma;
}
