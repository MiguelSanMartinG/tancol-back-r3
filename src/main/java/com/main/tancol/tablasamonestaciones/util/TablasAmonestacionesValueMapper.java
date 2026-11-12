package com.main.tancol.tablasamonestaciones.util;

import com.main.tancol.producto.entities.comerciales.ProductoComercialEntity;
import com.main.tancol.tablasamonestaciones.dto.response.ProductoAmonestacionDisponibleResponseDTO;
import com.main.tancol.tablasamonestaciones.dto.response.TablasAmonestacionesResponseDTO;
import com.main.tancol.tablasamonestaciones.entities.TablasAmonestacionesEntity;

public class TablasAmonestacionesValueMapper {

    private TablasAmonestacionesValueMapper() {
    }

    public static TablasAmonestacionesResponseDTO entityToResponse(TablasAmonestacionesEntity entity) {
        if (entity == null) {
            return null;
        }
        ProductoComercialEntity producto = entity.getProducto();
        String nombre = producto != null ? producto.getNombre() : null;
        return TablasAmonestacionesResponseDTO.builder()
                .id(entity.getId())
                .cantidadDesde(entity.getCantidadDesde())
                .cantidadHasta(entity.getCantidadHasta())
                .productoId(producto != null ? producto.getId() : null)
                .productoNombre(nombre)
                .productoAlias(producto != null ? producto.getAlias() : null)
                .descuento(entity.getDescuento())
                .textoDescuento(textoDescuento(nombre))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .enabled(entity.isEnabled())
                .build();
    }

    public static ProductoAmonestacionDisponibleResponseDTO productoDisponible(ProductoComercialEntity producto) {
        return ProductoAmonestacionDisponibleResponseDTO.builder()
                .idProducto(producto.getId())
                .nombre(producto.getNombre())
                .alias(producto.getAlias())
                .build();
    }

    public static String textoDescuento(String nombreProducto) {
        String nombre = nombreProducto == null || nombreProducto.isBlank() ? "producto" : nombreProducto.trim();
        return "Por cada " + nombre + " roto";
    }
}
