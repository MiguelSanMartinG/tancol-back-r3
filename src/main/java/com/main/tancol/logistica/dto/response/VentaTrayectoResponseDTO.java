package com.main.tancol.logistica.dto.response;

import com.main.tancol.almacen.dto.response.UnidadesMedidaResponseDTO;
import com.main.tancol.logistica.entities.bitacora.VentaTrayectoEntity;
import com.main.tancol.producto.dto.response.comercial.ProductoComercialResponseDTO;
import lombok.Builder;
import lombok.Value;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * DTO for {@link VentaTrayectoEntity}
 */
@Value
@Builder
public class VentaTrayectoResponseDTO implements Serializable {
    UUID idVenta;
    ProductoComercialResponseDTO producto;
    BigDecimal cantidadVendida;
    UnidadesMedidaResponseDTO unidades;
    BigDecimal cantidadSalida;
    BigDecimal cantidadLlegada;
    BigDecimal huecos;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    Set<ProductosFueraEspecificacionResponseDTO> productosFueraEspecificacion;
    Set<VentasEmpleadoResponseDTO> comisionesEmpleados;
    Set<MermaVentaTrayectoResponseDTO> mermas;
}
