package com.main.tancol.liquidacion.dto.response;

import com.main.tancol.cliente.dto.response.ClienteResponseDTO;
import com.main.tancol.liquidacion.entities.LiquidacionPartidaEntity;
import com.main.tancol.liquidacion.enums.FormaPagoLiquidacion;
import com.main.tancol.producto.dto.response.comercial.ProductoComercialResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for {@link LiquidacionPartidaEntity}
 */
@Value
@AllArgsConstructor
@Builder
public class LiquidacionPartidaResponseDTO implements Serializable {
    UUID idPartida;
    Long folio;
    ConceptosVentasResponseDTO conceptosVentas;
    ClienteResponseDTO cliente;
    ProductoComercialResponseDTO productoComercial;
    BigDecimal cantidad; // Cantidad de productos;
    BigDecimal precio; // Precio del producto;
    BigDecimal descuento; // Cuanto se le desconto por oferta

    BigDecimal importe; //Total de la partida;
    BigDecimal importeDescontado; //Cuanto ahorro;
    FormaPagoLiquidacion formaPago;
    Boolean factura; // Si requiere facturar la partida;
    Boolean cancelada;
    String motivoCancelacion;
    String usuarioCancelacion;
    LocalDateTime fechaCancelacion;

}