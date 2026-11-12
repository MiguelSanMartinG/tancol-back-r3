package com.main.tancol.orden.dto.request;

import com.main.tancol.orden.enums.FacturacionOrdenEstatus;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

/**
 * DTO for {@link com.main.tancol.orden.entities.OrdenEntity}
 */
@Value
@Builder
public class FacturacionOrdenRequestDTO implements Serializable {

    UUID orderId;
    String folioExterno;
    @NotNull
    FacturacionOrdenEstatus facturacionOrdenEstatus;
    List<PartidaOrdenRequestDTO> partidas;
    String comentarioFactura;

}