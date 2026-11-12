package com.main.tancol.medidores.dto.request;

import jakarta.validation.constraints.Positive;
import lombok.Value;

import java.math.BigDecimal;
import java.util.UUID;

@Value
public class BitacoraMedicionRequestDTO {
    UUID idMedidor;
    @Positive
    BigDecimal medicionActual;
    String comentario;
    BigDecimal liquidado;
    BigDecimal quedadoEnPipa;
    BigDecimal litrosMinuto;
}
