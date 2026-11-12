package com.main.tancol.tablascomisiones.util;

import com.main.tancol.producto.entities.comerciales.ProductoComercialEntity;
import com.main.tancol.tablascomisiones.dto.response.ProductoComisionDisponibleResponseDTO;
import com.main.tancol.tablascomisiones.dto.response.TablasComisionesResponseDTO;
import com.main.tancol.tablascomisiones.entities.TablasComisionesEntity;

public class TablasComisionesValueMapper {

    private TablasComisionesValueMapper() {
    }

    public static TablasComisionesResponseDTO entityToResponse(TablasComisionesEntity entity) {
        if (entity == null) {
            return null;
        }
        ProductoComercialEntity producto = entity.getProducto();
        String nombre = producto != null ? producto.getNombre() : null;
        return TablasComisionesResponseDTO.builder()
                .id(entity.getId())
                .ventaMinima(entity.getVentaMinima())
                .cantidadDesde(entity.getCantidadDesde())
                .cantidadHasta(entity.getCantidadHasta())
                .productoId(producto != null ? producto.getId() : null)
                .productoNombre(nombre)
                .productoAlias(producto != null ? producto.getAlias() : null)
                .comision(entity.getComision())
                .textoComision(textoComision(nombre))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .enabled(entity.isEnabled())
                .build();
    }

    public static ProductoComisionDisponibleResponseDTO productoDisponible(ProductoComercialEntity producto) {
        return ProductoComisionDisponibleResponseDTO.builder()
                .idProducto(producto.getId())
                .nombre(producto.getNombre())
                .alias(producto.getAlias())
                .build();
    }

    public static String textoComision(String nombreProducto) {
        String nombre = nombreProducto == null || nombreProducto.isBlank() ? "producto" : nombreProducto.trim();
        return "Por cada " + nombre + " vendido";
    }
}
