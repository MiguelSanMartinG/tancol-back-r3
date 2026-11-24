package com.main.tancol.reportes.dto.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.main.tancol.common.util.RangoFechasDeserializer;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Getter
public class ReporteTotalizadoClienteRequestDTO {
    private Set<UUID> idCliente;
    private Set<UUID> idProductosComerciales;
    @JsonDeserialize(using = RangoFechasDeserializer.Inicio.class)
    private LocalDateTime fechaInicio;
    @JsonDeserialize(using = RangoFechasDeserializer.Fin.class)
    private LocalDateTime fechaFin;
}
