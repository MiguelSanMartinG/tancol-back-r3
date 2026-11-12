package com.main.tancol.logistica.dto.response;

import com.main.tancol.almacen.dto.response.UnidadesMedidaResponseDTO;
import com.main.tancol.logistica.enums.EstatusVehiculo;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Builder
public class VehiculosResponseDTO {
    private UUID id;
    private String nombre;
    private String placas;
    private BigDecimal capacidadMaxima;
    private UnidadesMedidaResponseDTO unidadMedida;
    private String numeroPoliza;
    private LocalDateTime vigenciaPoliza;
    private String documentoPoliza;
    private EstatusVehiculo status;
    private String descripcion;

    private LocalDateTime cratedAt;
    private LocalDateTime updatedAt;
    private Boolean enabled;
    // Agrega m??s campos aqu??
}
