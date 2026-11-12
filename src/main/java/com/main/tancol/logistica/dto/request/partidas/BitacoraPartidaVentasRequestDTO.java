package com.main.tancol.logistica.dto.request.partidas;

import com.main.tancol.logistica.enums.EstadoVentaRuta;
import com.main.tancol.producto.dto.request.ProductosNoConsignadosRequestDTO;
import lombok.Value;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

@Value
public class BitacoraPartidaVentasRequestDTO{
     UUID idRuta;
     UUID idVehiculo;
     UUID idProductoComercial;
     UUID idBitacora;

//     @Max(value = 3, message = "Solo puede haber un maximo de 3")
//     Set<VentasEmpleadoRequestDTO> empleadosEnRuta;

     // Considerar quitar
     UUID idConductor;

     BigDecimal cantidadLlegada;
     BigDecimal cantidadSalida;
     BigDecimal huecos;
     BigDecimal rotos;
     BigDecimal donados;

     Set<VentasEmpleadoRequestDTO> comisionesEmpleados;
     Set<ProductosNoConsignadosRequestDTO> productosNoConsignados;

     String comentario;
     String idImagenFirma;
     EstadoVentaRuta estadoVentaRuta;

}
