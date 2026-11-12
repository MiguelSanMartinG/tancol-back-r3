package com.main.tancol.producto.dto.request;

import lombok.Value;

import java.math.BigDecimal;
import java.util.UUID;

@Value
public class ProductosNoConsignadosRequestDTO {
    // Agrega campos aqu??
    UUID idProductoRemanente; //Opcinal
    BigDecimal cantidad;
    Long idConcepto;

}
