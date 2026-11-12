package com.main.tancol.notificacion.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.util.List;

@Value
public class AsignacionNotificacionRolRequestDTO {

    @NotNull
    List<String> tipos;
}
