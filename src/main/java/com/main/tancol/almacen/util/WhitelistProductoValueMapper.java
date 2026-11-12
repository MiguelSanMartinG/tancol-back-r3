package com.main.tancol.almacen.util;

import com.main.tancol.almacen.dto.response.WhitelistProductoResponseDTO;
import com.main.tancol.almacen.entities.WhitelistProductoEmpleadoEntity;
import com.main.tancol.empleados.entities.EmpleadosEntity;
import com.main.tancol.empleados.util.DepartamentoMapper;
import com.main.tancol.producto.entities.insumos.ProductoBasicoEntity;

public class WhitelistProductoValueMapper {

    private WhitelistProductoValueMapper() {
    }

    public static WhitelistProductoResponseDTO toResponse(WhitelistProductoEmpleadoEntity entity) {
        EmpleadosEntity empleado = entity.getEmpleado();
        ProductoBasicoEntity producto = entity.getProducto();
        return WhitelistProductoResponseDTO.builder()
                .id(entity.getId())
                .idEmpleado(empleado == null ? null : empleado.getId())
                .nombreEmpleado(nombreEmpleado(empleado))
                .departamento(empleado == null || empleado.getDepartamento() == null
                        ? null
                        : DepartamentoMapper.DepartamentoEntityToResponseSimpleDTO(empleado.getDepartamento()))
                .idProducto(producto == null ? null : producto.getId())
                .nombreProducto(producto == null ? null : producto.getNombre())
                .sku(producto == null ? null : producto.getSku())
                .enabled(entity.isEnabled())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public static String nombreEmpleado(EmpleadosEntity empleado) {
        if (empleado == null) {
            return "";
        }
        StringBuilder nombre = new StringBuilder();
        append(nombre, empleado.getNombre());
        append(nombre, empleado.getPrimerApellido());
        append(nombre, empleado.getSegundoApellido());
        return nombre.toString().trim();
    }

    private static void append(StringBuilder nombre, String parte) {
        if (parte == null || parte.isBlank()) {
            return;
        }
        if (!nombre.isEmpty()) {
            nombre.append(' ');
        }
        nombre.append(parte.trim());
    }
}
