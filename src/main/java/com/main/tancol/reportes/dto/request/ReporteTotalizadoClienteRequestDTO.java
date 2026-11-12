package com.main.tancol.reportes.dto.request;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Getter
public class ReporteTotalizadoClienteRequestDTO {
    private Set<UUID> idCliente;
    private Set<UUID> idProductosComerciales;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
}
