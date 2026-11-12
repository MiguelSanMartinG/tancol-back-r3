package com.main.tancol.logistica.dto.response;

import com.main.tancol.empleados.dto.response.EmpleadosResponseDTO;
import com.main.tancol.logistica.enums.EstadoVentaRuta;
import com.main.tancol.producto.dto.response.ProductosNoConsignadosResponseDTO;
import com.main.tancol.producto.dto.response.comercial.ProductoComercialResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;


@AllArgsConstructor
@Getter
@Builder
public class BitacoraPartidaVentasResponseDTO {
    private UUID idPartida;

    private EmpleadosResponseDTO conductor;
    private ProductoComercialResponseDTO productoComercial;
    private VehiculosResponseDTO vehiculo;
    private RutasResponseDTO ruta;

    private BigDecimal cantidadLlegada;
    private BigDecimal cantidadSalida;
    private BigDecimal vendidos;
    private BigDecimal huecos;
    private BigDecimal rotos;

    private String comentario;
    private EstadoVentaRuta estadoVentaRuta;
    private String idImagenFirma;
//    private BigDecimal donados;
    private Set<ProductosNoConsignadosResponseDTO> rezago;
    private Set<VentasEmpleadoResponseDTO> comisionesEmpleados;


    // Agrega m??s campos aqu??
}
