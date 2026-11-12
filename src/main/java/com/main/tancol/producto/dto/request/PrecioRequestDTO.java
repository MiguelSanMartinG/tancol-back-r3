package com.main.tancol.producto.dto.request;

import com.main.tancol.producto.entities.PresentacionProductoBasico;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO for {@link PresentacionProductoBasico}
 */
@AllArgsConstructor
@Getter
public class PrecioRequestDTO implements Serializable {
    @NotNull
    private final UUID idPrecio;
    @NotNull
    private final BigDecimal precio;
}