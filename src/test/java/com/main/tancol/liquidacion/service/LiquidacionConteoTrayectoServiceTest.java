package com.main.tancol.liquidacion.service;

import com.main.tancol.common.exceptions.exeption.GenericExeption;
import com.main.tancol.empleados.entities.EmpleadosEntity;
import com.main.tancol.liquidacion.entities.LiquidacionEntity;
import com.main.tancol.liquidacion.entities.LiquidacionPartidaEntity;
import com.main.tancol.liquidacion.entities.LiquidacionTotalizadoEmpleadoEntity;
import com.main.tancol.liquidacion.entities.LiquidacionTotalizadoEntity;
import com.main.tancol.liquidacion.enums.EstadosLiquidacion;
import com.main.tancol.logistica.entities.bitacora.TrayectoEntity;
import com.main.tancol.logistica.entities.bitacora.VentaTrayectoEntity;
import com.main.tancol.logistica.entities.bitacora.comisiones.VentasEmpleadoEntity;
import com.main.tancol.producto.entities.comerciales.ProductoComercialEntity;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LiquidacionConteoTrayectoServiceTest {

    private final LiquidacionConteoTrayectoService service = new LiquidacionConteoTrayectoService();

    @Test
    void corregirSalidaYLlegadaActualizaChoferYAyudantesSinDuplicar() {
        ProductoComercialEntity producto = producto("Garrafón");
        EmpleadosEntity chofer = empleado("Chofer");
        EmpleadosEntity ayudante = empleado("Ayudante");
        TrayectoEntity viaje = trayecto(chofer, venta(producto, "10", "4", ayudante));

        LiquidacionTotalizadoEmpleadoEntity filaChofer = fila(chofer, "8", true);
        LiquidacionTotalizadoEmpleadoEntity filaAyudante = fila(ayudante, "8", false);
        filaChofer.getAyudantes().add(filaAyudante);
        filaAyudante.setChoferOrigen(filaChofer);
        LiquidacionTotalizadoEntity totalizado = totalizado(producto, filaChofer);

        LiquidacionPartidaEntity partida = LiquidacionPartidaEntity.builder()
                .cantidad(new BigDecimal("8"))
                .importe(new BigDecimal("80"))
                .build();
        LiquidacionEntity liquidacion = liquidacion(EstadosLiquidacion.PENDIENTE, totalizado);
        liquidacion.setLiquidacionPartida(new LinkedList<>(List.of(partida)));
        liquidacion.setTotalLiquidado(new BigDecimal("80"));

        service.sincronizar(liquidacion, viaje, Map.of(producto.getId(), new BigDecimal("8")), chofer.getId());

        assertEquals(1, liquidacion.getTotalesPorProductos().size());
        assertSame(viaje, totalizado.getTrayecto());
        assertCantidad("6", filaChofer.getCantidad());
        assertCantidad("6", filaAyudante.getCantidad());
        assertCantidad("8", partida.getCantidad());
        assertCantidad("80", partida.getImporte());
        assertCantidad("80", liquidacion.getTotalLiquidado());
    }

    @Test
    void segundoEnvioDelMismoTrayectoNoAgregaOtroTotalizado() {
        ProductoComercialEntity producto = producto("Garrafón");
        EmpleadosEntity chofer = empleado("Chofer");
        EmpleadosEntity ayudante = empleado("Ayudante");
        VentaTrayectoEntity venta = venta(producto, "10", "0", ayudante);
        TrayectoEntity viaje = trayecto(chofer, venta);
        LiquidacionEntity liquidacion = liquidacion(EstadosLiquidacion.PENDIENTE);

        service.incorporarTrayecto(liquidacion, viaje);
        venta.setCantidadLlegada(new BigDecimal("3"));
        service.incorporarTrayecto(liquidacion, viaje);

        assertEquals(1, liquidacion.getTotalesPorProductos().size());
        LiquidacionTotalizadoEntity totalizado = liquidacion.getTotalesPorProductos().get(0);
        assertSame(viaje, totalizado.getTrayecto());
        assertCantidad("7", totalizado.getEmpleadoTotalizadoLiquidacion().get(0).getCantidad());
        assertCantidad("7", totalizado.getEmpleadoTotalizadoLiquidacion().get(0).getAyudantes().get(0).getCantidad());
    }

    @Test
    void variasVentasDelMismoProductoSeSuman() {
        ProductoComercialEntity producto = producto("Garrafón");
        EmpleadosEntity chofer = empleado("Chofer");
        TrayectoEntity viaje = trayecto(chofer,
                venta(producto, "10", "0"),
                venta(producto, "5", "1"));
        LiquidacionEntity liquidacion = liquidacion(EstadosLiquidacion.BORRADOR);

        service.incorporarTrayecto(liquidacion, viaje);

        assertEquals(1, liquidacion.getTotalesPorProductos().size());
        assertCantidad("14", liquidacion.getTotalesPorProductos().get(0)
                .getEmpleadoTotalizadoLiquidacion().get(0).getCantidad());
    }

    @Test
    void liquidacionCerradaRechazaLaCorreccion() {
        for (EstadosLiquidacion estado : List.of(EstadosLiquidacion.FINALIZADA, EstadosLiquidacion.CANCELADA)) {
            ProductoComercialEntity producto = producto("Garrafón");
            EmpleadosEntity chofer = empleado("Chofer");
            TrayectoEntity viaje = trayecto(chofer, venta(producto, "10", "4"));
            LiquidacionTotalizadoEmpleadoEntity filaChofer = fila(chofer, "8", true);
            LiquidacionTotalizadoEntity totalizado = totalizado(producto, filaChofer);
            totalizado.setTrayecto(viaje);
            LiquidacionEntity liquidacion = liquidacion(estado, totalizado);

            GenericExeption error = assertThrows(GenericExeption.class,
                    () -> service.sincronizar(liquidacion, viaje, Map.of(producto.getId(), new BigDecimal("8")), chofer.getId()));

            assertEquals(LiquidacionConteoTrayectoService.MSG_CERRADA, error.getMessage());
            assertCantidad("8", filaChofer.getCantidad());
        }
    }

    @Test
    void coincidenciaAmbiguaRechazaLaCorreccion() {
        ProductoComercialEntity producto = producto("Garrafón");
        EmpleadosEntity chofer = empleado("Chofer");
        TrayectoEntity viaje = trayecto(chofer, venta(producto, "10", "4"));
        LiquidacionTotalizadoEntity primero = totalizado(producto, fila(chofer, "8", true));
        LiquidacionTotalizadoEntity segundo = totalizado(producto, fila(chofer, "8", true));
        LiquidacionEntity liquidacion = liquidacion(EstadosLiquidacion.PENDIENTE, primero, segundo);

        GenericExeption error = assertThrows(GenericExeption.class,
                () -> service.sincronizar(liquidacion, viaje, Map.of(producto.getId(), new BigDecimal("8")), chofer.getId()));

        assertEquals(LiquidacionConteoTrayectoService.MSG_AMBIGUO, error.getMessage());
        assertCantidad("8", primero.getEmpleadoTotalizadoLiquidacion().get(0).getCantidad());
        assertCantidad("8", segundo.getEmpleadoTotalizadoLiquidacion().get(0).getCantidad());
    }

    @Test
    void sinCoincidenciaRechazaLaCorreccion() {
        ProductoComercialEntity producto = producto("Garrafón");
        EmpleadosEntity chofer = empleado("Chofer");
        TrayectoEntity viaje = trayecto(chofer, venta(producto, "10", "4"));
        LiquidacionEntity liquidacion = liquidacion(EstadosLiquidacion.PENDIENTE);

        GenericExeption error = assertThrows(GenericExeption.class,
                () -> service.sincronizar(liquidacion, viaje, Map.of(producto.getId(), new BigDecimal("8")), chofer.getId()));

        assertEquals(LiquidacionConteoTrayectoService.MSG_NO_ENCONTRADO, error.getMessage());
        assertTrue(liquidacion.getTotalesPorProductos().isEmpty());
    }

    @Test
    void cambioDeRutaDeTrayectoEnviadoSeRechaza() {
        GenericExeption error = assertThrows(GenericExeption.class,
                () -> service.rechazarCambioDeRuta(true, true));
        assertEquals(LiquidacionConteoTrayectoService.MSG_RUTA, error.getMessage());
        service.rechazarCambioDeRuta(true, false);
        service.rechazarCambioDeRuta(false, true);
    }

    private void assertCantidad(String esperada, BigDecimal actual) {
        assertEquals(0, new BigDecimal(esperada).compareTo(actual));
    }

    private ProductoComercialEntity producto(String nombre) {
        return ProductoComercialEntity.builder().id(UUID.randomUUID()).nombre(nombre).build();
    }

    private EmpleadosEntity empleado(String nombre) {
        return EmpleadosEntity.builder().id(UUID.randomUUID()).nombre(nombre).build();
    }

    private TrayectoEntity trayecto(EmpleadosEntity chofer, VentaTrayectoEntity... ventas) {
        TrayectoEntity viaje = new TrayectoEntity();
        viaje.setIdTrayecto(UUID.randomUUID());
        viaje.setChofer(chofer);
        viaje.setVentaTrayecto(new LinkedHashSet<>(List.of(ventas)));
        return viaje;
    }

    private VentaTrayectoEntity venta(ProductoComercialEntity producto, String salida, String llegada, EmpleadosEntity... ayudantes) {
        VentaTrayectoEntity venta = VentaTrayectoEntity.builder()
                .idVenta(UUID.randomUUID())
                .producto(producto)
                .cantidadSalida(new BigDecimal(salida))
                .cantidadLlegada(new BigDecimal(llegada))
                .comisionesEmpleados(new LinkedHashSet<>())
                .build();
        for (EmpleadosEntity ayudante : ayudantes) {
            venta.getComisionesEmpleados().add(VentasEmpleadoEntity.builder()
                    .empleado(ayudante)
                    .comision(BigDecimal.ONE)
                    .trayectoOrigen(venta)
                    .build());
        }
        return venta;
    }

    private LiquidacionTotalizadoEmpleadoEntity fila(EmpleadosEntity empleado, String cantidad, boolean chofer) {
        return LiquidacionTotalizadoEmpleadoEntity.builder()
                .empleado(empleado)
                .cantidad(new BigDecimal(cantidad))
                .chofer(chofer)
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

    private LiquidacionEntity liquidacion(EstadosLiquidacion estado, LiquidacionTotalizadoEntity... totalizados) {
        LiquidacionEntity liquidacion = LiquidacionEntity.builder()
                .idLiquidacion(UUID.randomUUID())
                .estadosLiquidacion(estado)
                .totalesPorProductos(new LinkedList<>())
                .liquidacionPartida(new LinkedList<>())
                .totalLiquidado(BigDecimal.ZERO)
                .build();
        for (LiquidacionTotalizadoEntity totalizado : totalizados) {
            liquidacion.agregarTotalizado(totalizado);
        }
        return liquidacion;
    }
}
