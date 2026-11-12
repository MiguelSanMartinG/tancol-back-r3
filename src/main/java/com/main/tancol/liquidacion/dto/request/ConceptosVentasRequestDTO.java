package com.main.tancol.liquidacion.dto.request;

import lombok.Value;

@Value
public class ConceptosVentasRequestDTO {
    // Agrega campos aqu??
    String name;
    String concepto;
    boolean x;
    boolean cancelado;
    boolean descuento;
    boolean exclude;
    boolean enabled;
}
