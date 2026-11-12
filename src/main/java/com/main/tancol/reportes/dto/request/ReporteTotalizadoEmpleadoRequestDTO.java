package com.main.tancol.reportes.dto.request;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public class ReporteTotalizadoEmpleadoRequestDTO {
    private Set<UUID> idEmpleados;
    private Set<UUID> idProductosComerciales;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
}
