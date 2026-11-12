package com.main.tancol.medidores.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Builder
public class BitacoraMedicionResponseDTO {
    private UUID id;
    private BigDecimal medicionFinal;
    private BigDecimal medicionInicial;
    private MedidoresResponseDTO medidor;
    private BigDecimal consumo;
    private BigDecimal liquidado;
    private BigDecimal quedadoEnPipa;
    private BigDecimal litrosMinuto;
    private BigDecimal diferencia;
    private Boolean medidoHoy;
    private String usuario;
    private String comentario;
    private LocalDateTime cratedAt;
}
