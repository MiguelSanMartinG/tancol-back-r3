package com.main.tancol.empleados.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/** Domicilio actual del empleado (datos de RH). Todos los campos son opcionales. */
@Embeddable
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EmpleadoDomicilio {

    @Column(name = "dom_codigo_postal", length = 10)
    private String codigoPostal;

    @Column(name = "dom_tipo_vialidad", length = 100)
    private String tipoVialidad;

    @Column(name = "dom_nombre_vialidad")
    private String nombreVialidad;

    @Column(name = "dom_numero_exterior", length = 50)
    private String numeroExterior;

    @Column(name = "dom_numero_interior", length = 50)
    private String numeroInterior;

    @Column(name = "dom_colonia")
    private String colonia;

    @Column(name = "dom_localidad")
    private String localidad;

    @Column(name = "dom_municipio")
    private String municipio;

    @Column(name = "dom_entidad_federativa")
    private String entidadFederativa;

    @Column(name = "dom_calle1")
    private String calle1;

    @Column(name = "dom_calle2")
    private String calle2;
}
