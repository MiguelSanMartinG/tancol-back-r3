package com.main.tancol.proveedor.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

@Value
@Slf4j
public class ProveedorDTO implements Serializable {
    @NotBlank
    String nombre;
    @NotBlank
    String razonSocial;
}
