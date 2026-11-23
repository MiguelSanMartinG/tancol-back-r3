package com.main.tancol.empleados.enums;

/**
 * Documentos del expediente de RH y los datos que acepta cada uno.
 * fecha = emisión o inicio; vigencia = fecha de vigencia; mes = yyyy-MM; banco = banco y CLABE; estudios = nivel.
 */
public enum TipoDocumentoEmpleado {
    INE("INE", true, true, false, false, false),
    ACTA_NACIMIENTO("Acta de nacimiento", false, false, false, false, false),
    CURP("CURP", false, false, false, false, false),
    CONSTANCIA_SITUACION_FISCAL("Constancia de situación fiscal", false, false, true, false, false),
    COMPROBANTE_DOMICILIO("Comprobante de domicilio", false, false, true, false, false),
    IMSS("Hoja de asignación o vigencia IMSS", true, true, false, false, false),
    CARATULA_BANCARIA("Carátula bancaria", false, false, false, true, false),
    COMPROBANTE_ESTUDIOS("Comprobante de estudios", false, false, false, false, true),
    EXPERIENCIA_LABORAL("Experiencia laboral", false, false, false, false, false),
    CONTRATO("Contrato", true, true, false, false, false),
    LICENCIA_CONDUCIR("Licencia de conducir", false, true, false, false, false),
    ANTECEDENTES_NO_PENALES("Antecedentes no penales", false, true, false, false, false);

    private final String etiqueta;
    private final boolean fecha;
    private final boolean vigencia;
    private final boolean mes;
    private final boolean banco;
    private final boolean estudios;

    TipoDocumentoEmpleado(String etiqueta, boolean fecha, boolean vigencia, boolean mes, boolean banco, boolean estudios) {
        this.etiqueta = etiqueta;
        this.fecha = fecha;
        this.vigencia = vigencia;
        this.mes = mes;
        this.banco = banco;
        this.estudios = estudios;
    }

    public String etiqueta() { return etiqueta; }
    public boolean usaFecha() { return fecha; }
    public boolean usaVigencia() { return vigencia; }
    public boolean usaMes() { return mes; }
    public boolean usaBanco() { return banco; }
    public boolean usaEstudios() { return estudios; }
}
