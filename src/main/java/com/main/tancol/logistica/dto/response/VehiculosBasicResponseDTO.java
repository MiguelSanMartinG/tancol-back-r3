package com.main.tancol.logistica.dto.response;

import com.main.tancol.almacen.dto.response.UnidadesMedidaResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Builder
public class VehiculosBasicResponseDTO {
    private UUID id;
    private String nombre;
    private String placas;
    private BigDecimal capacidadMaxima;
    private UnidadesMedidaResponseDTO unidadMedida;
    private Boolean enabled;
    // Agrega m??s campos aqu??
}
