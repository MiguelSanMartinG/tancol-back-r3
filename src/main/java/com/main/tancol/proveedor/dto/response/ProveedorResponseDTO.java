package com.main.tancol.proveedor.dto.response;

import com.main.tancol.proveedor.entity.ProveedorEntity;
import com.main.tancol.proveedor.entity.TipoProveedorEntity;
import com.main.tancol.proveedor.enums.TipoContribuyente;
import com.main.tancol.proveedor.enums.TipoPago;
import jdk.jfr.Enabled;
import lombok.Builder;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO for {@link ProveedorEntity}
 */
@Value
@Builder
public class ProveedorResponseDTO implements Serializable {
    UUID idProveedor;
    String nombreComercial;
    String nombreTipoProveedor;
    UUID idTipoProveedor;
    List<TipoProveedorAsignado> tiposProveedor;
    TipoPago condicionPago;
    String correo;
    String telefono;
    InformacionFiscalResponseDTO infoFiscal;
    boolean enabled;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    @Value
    @Builder
    public static class TipoProveedorAsignado implements Serializable {
        UUID id;
        String nombre;
        String alias;
    }
}