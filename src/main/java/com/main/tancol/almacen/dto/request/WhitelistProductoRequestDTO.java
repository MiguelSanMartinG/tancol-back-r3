package com.main.tancol.almacen.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.util.UUID;

@Value
public class WhitelistProductoRequestDTO {
    @NotNull(message = "El empleado es requerido")
    UUID idEmpleado;
    @NotNull(message = "El producto es requerido")
    UUID idProducto;
}
