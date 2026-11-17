package com.main.tancol.almacen.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Body de PATCH /orden-interna/{id}/firma-recibe.
 * Clase mutable con constructor vacío (no @Value) para que Jackson la lea como objeto
 * aunque tenga un solo campo.
 */
@Getter
@Setter
@NoArgsConstructor
public class FirmaRecibeOrdenInternaRequestDTO {
    @NotBlank(message = "La imagen de la firma es requerida")
    private String idImagenFirmaRecibe;
}
