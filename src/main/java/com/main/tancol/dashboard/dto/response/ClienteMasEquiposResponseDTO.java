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
public class ClienteMasEquiposResponseDTO {
    private UUID idCliente;
    private String cliente;
    private int equiposAsignados;
    private int equiposActivos;
    private int equiposDevueltosEnPeriodo;
    private LocalDate ultimaAsignacion;
}
