package com.main.tancol.orden.dto.response;

import com.main.tancol.orden.enums.FacturacionOrdenEstatus;
import lombok.Builder;
import lombok.Value;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO for {@link com.main.tancol.orden.entities.OrdenEntity}
 */
@Value
@Builder
public class OrdenFacturacionResponseDTO implements Serializable {
    UUID id;
    String folioOrden;
    UUID proveedorId;
    String proveedorNombre;
    String proveedorRazonSocial;
    FacturacionOrdenEstatus estatusFactura;
    String folioExterno;
    LocalDateTime fechaFacturacion;
    LocalDateTime fechaSolicitud;
    LocalDateTime updatedAt;
    BigDecimal totalOrden;
    String comentarioFactura;
    List<PartidaOrdenResponseDTO> partidas;
}