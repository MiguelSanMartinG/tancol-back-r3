package com.main.tancol.metaventa.util;

import com.main.tancol.empleados.entities.EmpleadosEntity;
import com.main.tancol.metaventa.dto.response.MetaVentaResponseDTO;
import com.main.tancol.metaventa.entities.MetaVentaEmpleadoEntity;
import com.main.tancol.producto.entities.comerciales.ProductoComercialEntity;

public class MetaVentaValueMapper {

    private MetaVentaValueMapper() {
    }

    public static MetaVentaResponseDTO entityToResponse(MetaVentaEmpleadoEntity entity) {
        if (entity == null) {
            return null;
        }
        EmpleadosEntity empleado = entity.getEmpleado();
        ProductoComercialEntity producto = entity.getProducto();
        return MetaVentaResponseDTO.builder()
                .id(entity.getId())
                .empleadoId(empleado != null ? empleado.getId() : null)
                .empleadoNombre(nombreEmpleado(empleado))
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

    public static String nombreEmpleado(EmpleadosEntity empleado) {
        if (empleado == null) {
            return null;
        }
        String nombre = String.join(" ",
                texto(empleado.getNombre()),
                texto(empleado.getPrimerApellido()),
                texto(empleado.getSegundoApellido())).trim().replaceAll("\\s+", " ");
        return nombre.isBlank() ? null : nombre;
    }

    public static String etiquetaProducto(ProductoComercialEntity producto) {
        if (producto == null) {
            return "N/D";
        }
        if (producto.getAlias() != null && !producto.getAlias().isBlank()) {
            return producto.getAlias();
        }
        if (producto.getNombre() != null && !producto.getNombre().isBlank()) {
            return producto.getNombre();
        }
        return "N/D";
    }

    private static String texto(String valor) {
        return valor == null ? "" : valor;
    }
}
