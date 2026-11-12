package com.main.tancol.proveedor.dto.request;

import com.main.tancol.proveedor.entity.TipoImpuestoEntity;
import lombok.Value;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO for {@link TipoImpuestoEntity}
 */
@Value
public class TipoImpuestoRequestDTO implements Serializable {
    String nombre;
    BigDecimal porcentaje;
    Boolean enabled;

}