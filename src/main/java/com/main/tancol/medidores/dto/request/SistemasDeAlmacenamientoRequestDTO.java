package com.main.tancol.medidores.dto.request;

import lombok.Value;

import java.math.BigDecimal;
import java.util.UUID;

@Value
public class SistemasDeAlmacenamientoRequestDTO {
    // Agrega campos aqu??
    private UUID idDepartamento;
    private BigDecimal capacidad;
    private String nombre;
    private String alias;
}
