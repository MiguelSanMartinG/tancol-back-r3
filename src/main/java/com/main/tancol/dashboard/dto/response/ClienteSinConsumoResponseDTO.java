package com.main.tancol.dashboard.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteSinConsumoResponseDTO {
    private UUID idCliente;
    private String cliente;
    private LocalDate ultimaFechaConsumo;
    private Integer diasSinConsumir;
    private int equiposAsignados;
    private boolean nuncaHaConsumido;
}
