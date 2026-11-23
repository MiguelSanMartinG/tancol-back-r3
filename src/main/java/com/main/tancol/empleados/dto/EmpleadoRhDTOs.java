package com.main.tancol.empleados.dto;

import com.main.tancol.empleados.enums.NivelEstudios;
import com.main.tancol.empleados.enums.TipoDocumentoEmpleado;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/** DTOs de los datos de RH del empleado (se usan igual en request y response). */
public final class EmpleadoRhDTOs {

    private EmpleadoRhDTOs() {
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Domicilio {
        private String codigoPostal;
        private String tipoVialidad;
        private String nombreVialidad;
        private String numeroExterior;
        private String numeroInterior;
        private String colonia;
        private String localidad;
        private String municipio;
        private String entidadFederativa;
        private String calle1;
        private String calle2;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContactoEmergencia {
        private String nombre;
        private String parentesco;
        private String telefono;
        private String correo;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Documento {
        private TipoDocumentoEmpleado tipo;
        private String archivo;
        private LocalDate fecha;
        private LocalDate fechaVigencia;
        /** yyyy-MM */
        private String mes;
        private NivelEstudios nivelEstudios;
        private String banco;
        private String clabe;
    }
}
