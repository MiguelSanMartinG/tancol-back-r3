package com.main.tancol.proveedor.dto.request;

import com.main.tancol.proveedor.entity.ProveedorEntity;
import com.main.tancol.proveedor.entity.TipoProveedorEntity;
import com.main.tancol.proveedor.enums.TipoContribuyente;
import com.main.tancol.proveedor.enums.TipoPago;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

/**
 * DTO for {@link ProveedorEntity}
 */
@Value
public class ProveedorRequestDTO implements Serializable {
    @NotNull(message = "Debe tener un nombre")
    String nombreComercial;
    UUID idTipoProveedor;
    List<UUID> idsTipoProveedor;
    TipoPago condicionPago;
    String correo;
    String telefono;
    Boolean enabled;
    InformacionFiscalRequestDTO informacionFiscal;
}