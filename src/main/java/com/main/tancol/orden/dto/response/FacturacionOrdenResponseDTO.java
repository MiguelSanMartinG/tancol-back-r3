package com.main.tancol.orden.dto.response;

import com.main.tancol.orden.enums.FacturacionOrdenEstatus;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO for {@link com.main.tancol.orden.entities.OrdenEntity}
 */
@SuperBuilder(toBuilder = true)
@Getter
@Setter
public class FacturacionOrdenResponseDTO extends SolicitudOrdenResponseDTO implements Serializable {


    private FacturacionOrdenEstatus estatusFactura;
    private String folioExterno;
    private LocalDateTime fechaFacturacion;

    private String comentarioFactura;
}