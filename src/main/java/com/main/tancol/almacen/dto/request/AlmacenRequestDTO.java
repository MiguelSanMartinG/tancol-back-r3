package com.main.tancol.almacen.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link com.main.tancol.almacen.entities.Almacen}
 */
@Value
public class AlmacenRequestDTO implements Serializable {
    @NotBlank(message = "El nombre no puede estar vacio.")
    String nombre;
    Boolean status;
}