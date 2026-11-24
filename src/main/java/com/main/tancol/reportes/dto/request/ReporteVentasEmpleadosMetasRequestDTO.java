package com.main.tancol.reportes.dto.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.main.tancol.common.util.RangoFechasDeserializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReporteVentasEmpleadosMetasRequestDTO {
    @JsonDeserialize(using = RangoFechasDeserializer.Inicio.class)
    private LocalDateTime fechaInicio;
    @JsonDeserialize(using = RangoFechasDeserializer.Fin.class)
    private LocalDateTime fechaFin;
    private List<UUID> idProductos;
}
