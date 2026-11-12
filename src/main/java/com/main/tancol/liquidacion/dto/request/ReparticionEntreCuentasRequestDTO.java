package com.main.tancol.liquidacion.dto.request;

import lombok.Value;

import java.math.BigDecimal;

@Value
public class ReparticionEntreCuentasRequestDTO {
    // Agrega campos aqu??
    BigDecimal cuentaA;
    BigDecimal cuentaB;
    BigDecimal cuentaC;
}
