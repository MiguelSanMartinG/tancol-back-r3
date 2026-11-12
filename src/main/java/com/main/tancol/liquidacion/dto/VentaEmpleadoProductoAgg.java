package com.main.tancol.liquidacion.dto;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
public class VentaEmpleadoProductoAgg {

    private final UUID empleadoId;
    private final String nombre;
    private final String primerApellido;
    private final String segundoApellido;
    private final UUID productoId;
    private final String productoNombre;
    private final String productoAlias;
    private final BigDecimal cantidad;

    public VentaEmpleadoProductoAgg(UUID empleadoId,
                                    String nombre,
                                    String primerApellido,
                                    String segundoApellido,
                                    UUID productoId,
                                    String productoNombre,
                                    String productoAlias,
                                    BigDecimal cantidad) {
        this.empleadoId = empleadoId;
        this.nombre = nombre;
        this.primerApellido = primerApellido;
        this.segundoApellido = segundoApellido;
        this.productoId = productoId;
        this.productoNombre = productoNombre;
        this.productoAlias = productoAlias;
        this.cantidad = cantidad;
    }
}
