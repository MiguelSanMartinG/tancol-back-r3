package com.main.tancol.reportes.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.main.tancol.common.util.RangoFechasDeserializer;
import com.main.tancol.logistica.enums.TipoProceso;
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
public class ReporteBitacorasVentasRequestDTO {
    @JsonDeserialize(using = RangoFechasDeserializer.Inicio.class)
    private LocalDateTime fechaInicio;
    @JsonDeserialize(using = RangoFechasDeserializer.Fin.class)
    private LocalDateTime fechaFin;
    private String folio;

    @JsonAlias({"idRuta", "rutas"})
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private List<UUID> idRutas;

    @JsonAlias({"tipoProceso", "tipoProcesos", "proceso"})
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private List<TipoProceso> procesos;

    @JsonAlias({"idChofer", "choferes"})
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private List<UUID> idChoferes;

    @JsonAlias({"idAyudante", "ayudantes"})
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private List<UUID> idAyudantes;

    @JsonAlias({"idProducto", "productos"})
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private List<UUID> idProductos;

    @JsonAlias({"idVehiculo", "vehiculos"})
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private List<UUID> idVehiculos;

    private LocalDateTime fechaFirma;
    private Boolean rotos;

    @JsonAlias({"rotosPorMaquina", "roto_por_maquina", "rotasPorMaquina"})
    private Boolean rotoPorMaquina;

    private Boolean fueraEspecificacion;
}
