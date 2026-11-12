package com.main.tancol.cliente.dto.response;

import com.main.tancol.cliente.enums.TipoActualizacionPrecio;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Builder
public class HistoricoPrecioClienteResponseDTO {
    private final UUID id;
    private final UUID operacionId;
    private final UUID idProducto;
    private final String producto;
    private final BigDecimal precioAnterior;
    private final BigDecimal precioNuevo;
    private final String usuario;
    private final LocalDateTime fecha;
    private final TipoActualizacionPrecio tipoActualizacion;
}
