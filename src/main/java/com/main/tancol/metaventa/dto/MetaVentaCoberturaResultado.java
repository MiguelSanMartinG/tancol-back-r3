package com.main.tancol.metaventa.dto;

import com.main.tancol.metaventa.entities.MetaVentaEmpleadoEntity;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class MetaVentaCoberturaResultado {
    private MetaVentaEmpleadoEntity meta;
    private LocalDateTime periodoInicio;
    private LocalDateTime periodoFin;
    private BigDecimal cantidadMinima;
    private BigDecimal cantidadVendida;
    private boolean cubierto;
}
