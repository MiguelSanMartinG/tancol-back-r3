package com.main.tancol.metaventa.util;

import com.main.tancol.logistica.entities.rutas.RutasEntity;
import com.main.tancol.metaventa.dto.response.MetaVentaRutaResponseDTO;
import com.main.tancol.metaventa.entities.MetaVentaRutaEntity;
import com.main.tancol.producto.entities.comerciales.ProductoComercialEntity;

public class MetaVentaRutaValueMapper {

    private MetaVentaRutaValueMapper() {
    }

    public static MetaVentaRutaResponseDTO entityToResponse(MetaVentaRutaEntity entity) {
        if (entity == null) {
            return null;
        }
        RutasEntity ruta = entity.getRuta();
        ProductoComercialEntity producto = entity.getProducto();
        return MetaVentaRutaResponseDTO.builder()
                .id(entity.getId())
                .rutaId(ruta != null ? ruta.getId() : null)
                .rutaNombre(ruta != null ? ruta.getNombre() : null)
                .rutaNumero(ruta != null ? ruta.getNumero() : null)
                .productoId(producto != null ? producto.getId() : null)
                .productoNombre(producto != null ? producto.getNombre() : null)
                .productoAlias(producto != null ? producto.getAlias() : null)
                .cantidadMinima(entity.getCantidadMinima())
                .periodo(entity.getPeriodo())
                .activa(entity.isActiva())
                .fechaInicio(entity.getFechaInicio())
                .fechaCierre(entity.getFechaCierre())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .enabled(entity.isEnabled())
                .build();
    }
}
