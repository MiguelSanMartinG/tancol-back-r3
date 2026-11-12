package com.main.tancol.logistica.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.main.tancol.logistica.dto.request.partidas.VentasEmpleadoRequestDTO;
import lombok.Value;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

@Value
@JsonIgnoreProperties(ignoreUnknown = true)
public class VentaTrayectoRequestDTO {
    UUID idVenta;
    UUID idProducto;

    BigDecimal cantidadLlegada;
    BigDecimal cantidadSalida;

    BigDecimal huecos;

    Set<VentasEmpleadoRequestDTO> comisionesEmpleado;
    Set<ProductosFueraEspecificacionRequestDTO> productosFueraEspecificacion;
    Set<MermaVentaTrayectoRequestDTO> mermas;
}
