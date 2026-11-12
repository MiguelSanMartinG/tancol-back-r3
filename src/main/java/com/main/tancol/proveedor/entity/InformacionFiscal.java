package com.main.tancol.proveedor.entity;

import com.main.tancol.proveedor.enums.TipoContribuyente;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InformacionFiscal {
    @Column(name = "tipo_contribuyente_fiscal")
    private TipoContribuyente tipoContribuyenteFiscal;
    @Column(name = "razon_social_fiscal")
    private  String razonSocial;
    private String rfc;
    private  String codigoPostal;
    private  String tipoVialidad;
    private  String nombreVialidad;
    private  String numeroExterior;
    private  String numeroInterior;
    private  String colonia;
    private  String localidad;
    private  String municipio;
    private String entidadFederativa;
    private String calle1;
    private String calle2;

}
