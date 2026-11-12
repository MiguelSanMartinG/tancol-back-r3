package com.main.tancol.permisos.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.util.UUID;

@Value
public class AsignacionPermisoAvanzadoRequestDTO {

    @NotNull
    UUID empleadoId;

    /** Código del permiso (ej. CANCELAR_LIQUIDACION). Alternativa a permisoId. */
    String codigo;

    /** Id del permiso en catálogo. Alternativa a codigo. */
    UUID permisoId;
}
