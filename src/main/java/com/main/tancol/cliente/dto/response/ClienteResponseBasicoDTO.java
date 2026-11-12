package com.main.tancol.cliente.dto.response;

import com.main.tancol.proveedor.dto.response.InformacionFiscalResponseDTO;
import com.main.tancol.proveedor.enums.TipoPago;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Getter
@SuperBuilder
public class ClienteResponseBasicoDTO {
    private UUID idCliente;
    private String nombreComercial;
    private TipoClienteSimpleResponseDTO tipoDeCliente;
    private boolean enabled;
    private boolean esEmpleado;
}
