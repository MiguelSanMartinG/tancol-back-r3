package com.main.tancol.medidores.dto.request;

import com.main.tancol.medidores.enums.TiposAgua;
import lombok.Value;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Value
public class MedidoresRequestDTO {
    // Agrega campos aqu??

    String nombre;
    String alias;
    List<UUID> idsSistemasAlmacenamiento;
    UUID unidadMedicion;
    TiposAgua tipoAgua;
    BigDecimal medicionInicial;
    List<UUID> medidoresHijo;
    /** Consumo diario en litros a partir del cual se avisa al capturar la lectura. Null = sin alerta. */
    BigDecimal alertaConsumoLitros;
}
