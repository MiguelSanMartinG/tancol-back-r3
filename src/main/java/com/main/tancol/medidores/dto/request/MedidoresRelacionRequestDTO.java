package com.main.tancol.medidores.dto.request;

import lombok.Value;

import java.util.UUID;
@Value
public class MedidoresRelacionRequestDTO {
    UUID idMedidorPadre;
    UUID idMedidorHijo;
}
