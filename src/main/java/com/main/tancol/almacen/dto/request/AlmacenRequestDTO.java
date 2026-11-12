package com.main.tancol.almacen.dto.request;

import com.main.tancol.almacen.entities.AlmacenEntity;
import jakarta.validation.constraints.NotBlank;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link AlmacenEntity}
 */
@Value
public class AlmacenRequestDTO implements Serializable {
    @NotBlank(message = "El nombre no puede estar vacio.")
    String nombre;
    Boolean status;
}