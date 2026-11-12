package com.main.tancol.logistica.dto.request;

import lombok.Value;

import java.math.BigDecimal;
import java.util.UUID;

@Value
public class MermaVentaTrayectoRequestDTO {
    UUID idMerma;
    UUID idEmpleado;
    BigDecimal cantidad;
    Boolean rotoPorMaquina;
    String idImagenFirma;
}
