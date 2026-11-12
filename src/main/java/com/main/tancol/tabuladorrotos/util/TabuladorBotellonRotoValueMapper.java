package com.main.tancol.tabuladorrotos.util;

import com.main.tancol.almacen.entities.UnidadesMedidaEntity;
import com.main.tancol.empleados.entities.EmpleadosEntity;
import com.main.tancol.producto.entities.comerciales.ProductoComercialEntity;
import com.main.tancol.tabuladorrotos.dto.response.TabuladorBotellonRotoResponseDTO;
import com.main.tancol.tabuladorrotos.entities.TabuladorBotellonRotoEntity;
import org.springframework.util.StringUtils;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TabuladorBotellonRotoValueMapper {

    private TabuladorBotellonRotoValueMapper() {
    }

    public static TabuladorBotellonRotoResponseDTO entityToResponse(TabuladorBotellonRotoEntity entity) {
        if (entity == null) {
            return null;
        }
        EmpleadosEntity empleado = entity.getEmpleado();
        ProductoComercialEntity producto = entity.getProducto();
        UnidadesMedidaEntity unidad = entity.getUnidadMedida();
        return TabuladorBotellonRotoResponseDTO.builder()
                .id(entity.getId())
                .empleadoId(empleado != null ? empleado.getId() : null)
                .empleadoNombre(nombreEmpleado(empleado))
                .periodo(entity.getPeriodo())
                .tolerancia(entity.getTolerancia())
                .productoId(producto != null ? producto.getId() : null)
                .productoNombre(producto != null ? producto.getNombre() : null)
                .unidadMedidaId(unidad != null ? unidad.getIdUnidadesMedida() : null)
                .unidadMedidaNombre(unidad != null ? unidad.getNombre() : null)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .enabled(entity.isEnabled())
                .build();
    }

    public static String nombreEmpleado(EmpleadosEntity empleado) {
        if (empleado == null) {
            return null;
        }
        String nombre = Stream.of(empleado.getNombre(), empleado.getPrimerApellido(), empleado.getSegundoApellido())
                .filter(StringUtils::hasText)
                .map(String::trim)
                .collect(Collectors.joining(" "));
        return nombre.isBlank() ? null : nombre;
    }
}
