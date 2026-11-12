package com.main.tancol.proveedor.dto.request;

import com.main.tancol.proveedor.entity.ProveedorEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link ProveedorEntity}
 */
@Value
public class ProveedorRequestDTO implements Serializable {
    @NotNull(message = "Debe tener un nombre")
    String nombre;
    @NotBlank
    String razonSocial;
    boolean enabled;

}