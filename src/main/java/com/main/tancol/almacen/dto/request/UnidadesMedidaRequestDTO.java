package com.main.tancol.almacen.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Value
public class UnidadesMedidaRequestDTO {
    @NotBlank(message = "El nombre es requerido")
    String nombre;
    String alias;
}
