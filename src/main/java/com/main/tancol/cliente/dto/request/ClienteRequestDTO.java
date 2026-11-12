package com.main.tancol.cliente.dto.request;

import com.main.tancol.proveedor.dto.request.InformacionFiscalRequestDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.UUID;

@Value
public class ClienteRequestDTO {
    @NotBlank
    String nombreComercial;
    @NotNull
    UUID idTipoDeCliente;
    @NotBlank
    String tipoPago;
    Long diasDeCredito;
    @NotNull
    LocalDateTime fechaAlta;
    String correo;
    String telefono;
    UUID idRuta;
    InformacionFiscalRequestDTO informacionFiscal;
    String comentario;
    // Agrega campos aqu??
}
