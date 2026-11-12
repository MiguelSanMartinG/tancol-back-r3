package com.main.tancol.proveedor.dto.request;

import com.main.tancol.proveedor.enums.TipoContribuyente;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

/**
 * DTO for {@link com.main.tancol.proveedor.entity.InformacionFiscal}
 */
@AllArgsConstructor
@Getter
@Builder
public class InformacionFiscalRequestDTO implements Serializable {
    private final TipoContribuyente tipoContribuyente;
    private final String razonSocial;
    private final String rfc;
    private final String codigoPostal;
    private final String tipoVialidad;
    private final String nombreVialidad;
    private final String numeroExterior;
    private final String numeroInterior;
    private final String colonia;
    private final String localidad;
    private final String municipio;
    private final String entidadFederativa;
    private final String calle1;
    private final String calle2;

}