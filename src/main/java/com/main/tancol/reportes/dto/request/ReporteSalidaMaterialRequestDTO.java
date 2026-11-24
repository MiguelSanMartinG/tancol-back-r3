package com.main.tancol.reportes.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReporteSalidaMaterialRequestDTO {
    @JsonDeserialize(using = RangoFechasDeserializer.Inicio.class)
    private LocalDateTime fechaInicio;
    @JsonDeserialize(using = RangoFechasDeserializer.Fin.class)
    private LocalDateTime fechaFin;

    @JsonAlias({"area", "uso", "usos"})
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private List<String> areas;

    @JsonAlias({"idEmpleado", "empleados"})
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private List<UUID> idEmpleados;

    @JsonAlias({"idInsumo", "idProducto", "idProductos", "insumos"})
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private List<UUID> idInsumos;

    @JsonAlias({"idAlmacen", "almacenes"})
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private List<UUID> idAlmacenes;
}
