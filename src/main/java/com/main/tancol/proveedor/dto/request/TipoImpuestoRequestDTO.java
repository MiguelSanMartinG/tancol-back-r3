package com.main.tancol.proveedor.dto.request;

import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link com.main.tancol.proveedor.entity.TipoImpuesto}
 */
@Value
public class TipoImpuestoRequestDTO implements Serializable {
    String nombre;
    Double porcentaje;
}