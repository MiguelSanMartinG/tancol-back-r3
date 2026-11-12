package com.main.tancol.liquidacion.service;

import com.main.tancol.empleados.entities.EmpleadosEntity;
import com.main.tancol.liquidacion.entities.LiquidacionEntity;
import com.main.tancol.liquidacion.entities.LiquidacionPartidaEntity;
import com.main.tancol.liquidacion.entities.LiquidacionTotalizadoEmpleadoEntity;
import com.main.tancol.liquidacion.entities.LiquidacionTotalizadoEntity;
import com.main.tancol.liquidacion.enums.EstadosLiquidacion;
import com.main.tancol.producto.entities.comerciales.ProductoComercialEntity;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class LiquidacionRebalanceServiceTest {

    private final LiquidacionRebalanceService service = new LiquidacionRebalanceService();

    @Test
    void reemplazoDelMismoProductoConservaChoferYCantidad() {
        ProductoComercialEntity producto = producto("E20");
        LiquidacionTotalizadoEmpleadoEntity chofer = chofer(new BigDecimal("10"));
        LiquidacionTotalizadoEntity totalizado = totalizado(producto, chofer);

        LiquidacionPartidaEntity vieja = partida(producto, "10", "100", true);
        LiquidacionPartidaEntity nueva = partida(producto, "10", "100", false);
        LiquidacionEntity liquidacion = liquidacion(totalizado, vieja, nueva);

        service.rebalancePorReemplazoPartida(liquidacion, vieja, nueva);

        assertEquals(1, liquidacion.getTotalesPorProductos().size());
        assertEquals(new BigDecimal("10"), chofer.getCantidad());
        assertEquals(new BigDecimal("100"), liquidacion.getTotalLiquidado());
    }

    @Test
    void reemplazoAjustaDeltaDeCantidad() {
        ProductoComercialEntity producto = producto("E20");
        LiquidacionTotalizadoEmpleadoEntity chofer = chofer(new BigDecimal("10"));
        LiquidacionTotalizadoEntity totalizado = totalizado(producto, chofer);

        LiquidacionPartidaEntity vieja = partida(producto, "10", "100", true);
        LiquidacionPartidaEntity nueva = partida(producto, "7", "70", false);
        LiquidacionEntity liquidacion = liquidacion(totalizado, vieja, nueva);

        service.rebalancePorReemplazoPartida(liquidacion, vieja, nueva);

        assertEquals(new BigDecimal("7"), chofer.getCantidad());
        assertEquals(new BigDecimal("70"), liquidacion.getTotalLiquidado());
    }

    @Test
    void cancelacionSinReemplazoQuitaTotalizadoVacio() {
        ProductoComercialEntity producto = producto("E20");
        LiquidacionTotalizadoEmpleadoEntity chofer = chofer(new BigDecimal("10"));
        LiquidacionTotalizadoEntity totalizado = totalizado(producto, chofer);

        LiquidacionPartidaEntity vieja = partida(producto, "10", "0", true);
        LiquidacionEntity liquidacion = liquidacion(totalizado, vieja);

        service.rebalancePorCancelacionPartida(liquidacion, vieja);

        assertEquals(0, liquidacion.getTotalesPorProductos().size());
        assertEquals(BigDecimal.ZERO, liquidacion.getTotalLiquidado());
    }

    @Test
    void reemplazoDeOtroProductoClonaChofer() {
        ProductoComercialEntity origen = producto("E20");
        ProductoComercialEntity destino = producto("PG");
        LiquidacionTotalizadoEmpleadoEntity chofer = chofer(new BigDecimal("10"));
        chofer.setEmpleado(EmpleadosEntity.builder().id(UUID.randomUUID()).nombre("Juan").build());
        LiquidacionTotalizadoEntity totalizado = totalizado(origen, chofer);

        LiquidacionPartidaEntity vieja = partida(origen, "10", "100", true);
        LiquidacionPartidaEntity nueva = partida(destino, "4", "40", false);
        LiquidacionEntity liquidacion = liquidacion(totalizado, vieja, nueva);

        service.rebalancePorReemplazoPartida(liquidacion, vieja, nueva);

        assertEquals(1, liquidacion.getTotalesPorProductos().size());
        LiquidacionTotalizadoEntity nuevoTotalizado = liquidacion.getTotalesPorProductos().get(0);
        assertEquals(destino.getId(), nuevoTotalizado.getProductoComercial().getId());
        assertFalse(nuevoTotalizado.getEmpleadoTotalizadoLiquidacion().isEmpty());
        assertEquals(new BigDecimal("4"), nuevoTotalizado.getEmpleadoTotalizadoLiquidacion().get(0).getCantidad());
        assertEquals(new BigDecimal("40"), liquidacion.getTotalLiquidado());
    }

    private ProductoComercialEntity producto(String nombre) {
        return ProductoComercialEntity.builder().id(UUID.randomUUID()).nombre(nombre).build();
    }

    private LiquidacionTotalizadoEmpleadoEntity chofer(BigDecimal cantidad) {
        return LiquidacionTotalizadoEmpleadoEntity.builder()
                .empleado(new EmpleadosEntity())
                .cantidad(cantidad)
                .chofer(true)
                .ayudantes(new LinkedList<>())
                .build();
    }

    private LiquidacionTotalizadoEntity totalizado(ProductoComercialEntity producto, LiquidacionTotalizadoEmpleadoEntity chofer) {
        LiquidacionTotalizadoEntity totalizado = LiquidacionTotalizadoEntity.builder()
                .id(UUID.randomUUID())
                .productoComercial(producto)
                .empleadoTotalizadoLiquidacion(new LinkedList<>(List.of(chofer)))
                .build();
        chofer.setOrigenTotalizado(totalizado);
        return totalizado;
    }

    private LiquidacionPartidaEntity partida(ProductoComercialEntity producto, String cantidad, String importe, boolean cancelada) {
        return LiquidacionPartidaEntity.builder()
                .productoComercial(producto)
                .cantidad(new BigDecimal(cantidad))
                .importe(new BigDecimal(importe))
                .cancelada(cancelada)
                .build();
    }

    private LiquidacionEntity liquidacion(LiquidacionTotalizadoEntity totalizado, LiquidacionPartidaEntity... partidas) {
        LiquidacionEntity liquidacion = LiquidacionEntity.builder()
                .estadosLiquidacion(EstadosLiquidacion.FINALIZADA)
                .totalesPorProductos(new LinkedList<>(List.of(totalizado)))
                .liquidacionPartida(new LinkedList<>(List.of(partidas)))
                .build();
        totalizado.setLiquidacionOrigen(liquidacion);
        return liquidacion;
    }
}
