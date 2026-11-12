package com.main.tancol.cliente.dto.response;

import com.main.tancol.logistica.dto.response.RutasResponseDTO;
import com.main.tancol.proveedor.dto.response.InformacionFiscalResponseDTO;
import com.main.tancol.proveedor.enums.TipoPago;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Getter
@SuperBuilder
public class ClienteResponseDTO {
    private UUID id;
    private String nombreComercial;
    private Integer noCliente;
//    private TipoContribuyente tipoContribuyente;
    private TipoClienteSimpleResponseDTO tipoDeCliente;
    private TipoPago tipoPago;
    private Long diasDeCredito;
    private String correo;
    private String telefono;
    private LocalDateTime fechaAlta;
    private InformacionFiscalResponseDTO informacionFiscal;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean enabled;
    private boolean esEmpleado;
    private RutasResponseDTO rutaAsociada;
    private String comentario;
    private List<String> historialNombresComerciales;
    private List<String> historialRazonesSociales;
}
