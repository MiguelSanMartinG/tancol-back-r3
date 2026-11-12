package com.main.tancol.almacen.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.util.List;
import java.util.UUID;

@Value
public class WhitelistAsignacionMasivaRequestDTO {
    @NotNull(message = "El empleado es requerido")
    UUID idEmpleado;
    @NotNull(message = "La lista de productos es requerida")
    List<UUID> idProductos;
    Boolean reemplazar;
}
