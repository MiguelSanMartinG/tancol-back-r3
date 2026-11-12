package com.main.tancol.reportes.service;

import com.main.tancol.empleados.entities.EmpleadosEntity;
import com.main.tancol.metaventa.entities.MetaVentaEmpleadoEntity;
import com.main.tancol.metaventa.repositories.MetaVentaEmpleadoRepository;
import com.main.tancol.producto.entities.comerciales.ProductoComercialEntity;
import com.main.tancol.liquidacion.dto.VentaEmpleadoProductoAgg;
import com.main.tancol.liquidacion.repositories.EmpleadoTotalizadoLiquidacionRepository;
import com.main.tancol.reportes.dto.request.ReporteVentasEmpleadosMetasRequestDTO;
import com.main.tancol.reportes.dto.response.ReporteVentasEmpleadosMetasResponseDTO;
import com.main.tancol.tablascomisiones.entities.TablasComisionesEntity;
import com.main.tancol.tablascomisiones.repositories.TablasComisionesRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReporteVentasEmpleadosMetasServiceTest {

    @Mock
    private EmpleadoTotalizadoLiquidacionRepository ventasRepository;

    @Mock
    private MetaVentaEmpleadoRepository metaRepository;

    @Mock
    private TablasComisionesRepository comisionesRepository;

    @InjectMocks
    private ReporteVentasEmpleadosMetasService service;

    @Test
    void calculaAlcanceYComisionConMetaYTabla() {
        UUID empleadoId = UUID.randomUUID();
        UUID productoId = UUID.randomUUID();
        LocalDateTime inicio = LocalDateTime.of(2026, 10, 6, 0, 0);
        LocalDateTime fin = inicio.plusDays(7);
        when(metaRepository.findVigentesPorProductos(List.of(productoId), inicio, fin))
                .thenReturn(List.of(meta(empleadoId, "Ana", "López", productoId, "Garrafón", "GAR", "20", true, inicio)));
        when(comisionesRepository.findByProductoIdInAndDeletedFalse(List.of(productoId)))
                .thenReturn(List.of(tabla(productoId, "1.50")));
        when(ventasRepository.sumarVentasPorEmpleadoYProducto(List.of(productoId), inicio, fin))
                .thenReturn(List.of(venta(empleadoId, "Ana", "López", productoId, "Garrafón", "GAR", "35")));

        List<ReporteVentasEmpleadosMetasResponseDTO> filas = service.generar(filtro(inicio, fin, productoId));

        assertEquals(1, filas.size());
        ReporteVentasEmpleadosMetasResponseDTO fila = filas.get(0);
        assertEquals("Ana López", fila.getEmpleadoNombre());
        assertEquals(0, new BigDecimal("20").compareTo(fila.getVentaMinima()));
        assertEquals(0, new BigDecimal("35").compareTo(fila.getVentaSemana()));
        assertEquals(0, new BigDecimal("15").compareTo(fila.getAlcanceObjetivo()));
        assertEquals(0, new BigDecimal("22.50").compareTo(fila.getComisionSugerida()));
    }

    @Test
    void sinTablaDeComisionesLaComisionEsNull() {
        UUID empleadoId = UUID.randomUUID();
        UUID productoId = UUID.randomUUID();
        LocalDateTime inicio = LocalDateTime.of(2026, 10, 6, 0, 0);
        LocalDateTime fin = inicio.plusDays(6);
        when(metaRepository.findVigentesPorProductos(any(), any(), any()))
                .thenReturn(List.of(meta(empleadoId, "Luis", "Paz", productoId, "Botellón", "BOT", "10", true, inicio)));
        when(comisionesRepository.findByProductoIdInAndDeletedFalse(any())).thenReturn(List.of());
        when(ventasRepository.sumarVentasPorEmpleadoYProducto(any(), any(), any()))
                .thenReturn(List.of(venta(empleadoId, "Luis", "Paz", productoId, "Botellón", "BOT", "12")));

        ReporteVentasEmpleadosMetasResponseDTO fila = service.generar(filtro(inicio, fin, productoId)).get(0);

        assertEquals(0, new BigDecimal("2").compareTo(fila.getAlcanceObjetivo()));
        assertNull(fila.getComisionSugerida());
    }

    @Test
    void sinMetaNoCalculaAlcanceNiComision() {
        UUID empleadoId = UUID.randomUUID();
        UUID productoId = UUID.randomUUID();
        LocalDateTime inicio = LocalDateTime.of(2026, 10, 6, 0, 0);
        LocalDateTime fin = inicio.plusDays(6);
        when(metaRepository.findVigentesPorProductos(any(), any(), any())).thenReturn(List.of());
        when(comisionesRepository.findByProductoIdInAndDeletedFalse(any()))
                .thenReturn(List.of(tabla(productoId, "2.00")));
        when(ventasRepository.sumarVentasPorEmpleadoYProducto(any(), any(), any()))
                .thenReturn(List.of(venta(empleadoId, "Eva", "Ruiz", productoId, "Garrafón", "GAR", "8")));

        ReporteVentasEmpleadosMetasResponseDTO fila = service.generar(filtro(inicio, fin, productoId)).get(0);

        assertEquals("Eva Ruiz", fila.getEmpleadoNombre());
        assertNull(fila.getVentaMinima());
        assertEquals(0, new BigDecimal("8").compareTo(fila.getVentaSemana()));
        assertNull(fila.getAlcanceObjetivo());
        assertNull(fila.getComisionSugerida());
    }

    @Test
    void metaSinVentasDejaLaSemanaEnCero() {
        UUID empleadoId = UUID.randomUUID();
        UUID productoId = UUID.randomUUID();
        LocalDateTime inicio = LocalDateTime.of(2026, 10, 6, 0, 0);
        LocalDateTime fin = inicio.plusDays(6);
        when(metaRepository.findVigentesPorProductos(any(), any(), any()))
                .thenReturn(List.of(meta(empleadoId, "Omar", "Diaz", productoId, "Garrafón", "GAR", "10", true, inicio)));
        when(comisionesRepository.findByProductoIdInAndDeletedFalse(any()))
                .thenReturn(List.of(tabla(productoId, "1.00")));
        when(ventasRepository.sumarVentasPorEmpleadoYProducto(any(), any(), any())).thenReturn(List.of());

        ReporteVentasEmpleadosMetasResponseDTO fila = service.generar(filtro(inicio, fin, productoId)).get(0);

        assertEquals(0, BigDecimal.ZERO.compareTo(fila.getVentaSemana()));
        assertEquals(0, new BigDecimal("-10").compareTo(fila.getAlcanceObjetivo()));
        assertEquals(0, new BigDecimal("-10.00").compareTo(fila.getComisionSugerida()));
    }

    @Test
    void prefiereLaMetaActivaCuandoHayDosVigentes() {
        UUID empleadoId = UUID.randomUUID();
        UUID productoId = UUID.randomUUID();
        LocalDateTime inicio = LocalDateTime.of(2026, 10, 6, 0, 0);
        LocalDateTime fin = inicio.plusDays(6);
        when(metaRepository.findVigentesPorProductos(any(), any(), any())).thenReturn(List.of(
                meta(empleadoId, "Ana", "López", productoId, "Garrafón", "GAR", "5", false, inicio.minusDays(1)),
                meta(empleadoId, "Ana", "López", productoId, "Garrafón", "GAR", "20", true, inicio)));
        when(comisionesRepository.findByProductoIdInAndDeletedFalse(any())).thenReturn(List.of());
        when(ventasRepository.sumarVentasPorEmpleadoYProducto(any(), any(), any())).thenReturn(List.of());

        ReporteVentasEmpleadosMetasResponseDTO fila = service.generar(filtro(inicio, fin, productoId)).get(0);

        assertEquals(0, new BigDecimal("20").compareTo(fila.getVentaMinima()));
    }

    @Test
    void rechazaUnRangoMayorAOchoDias() {
        LocalDateTime inicio = LocalDateTime.of(2026, 10, 1, 0, 0);
        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> service.generar(filtro(inicio, inicio.plusDays(8).plusSeconds(1), UUID.randomUUID())));

        assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());
        assertTrue(error.getReason().contains("8 días"));
        verifyNoInteractions(ventasRepository, metaRepository, comisionesRepository);
    }

    @Test
    void aceptaUnRangoDeOchoDias() {
        LocalDateTime inicio = LocalDateTime.of(2026, 10, 1, 0, 0);
        LocalDateTime fin = inicio.plusDays(8);
        UUID productoId = UUID.randomUUID();
        when(metaRepository.findVigentesPorProductos(any(), any(), any())).thenReturn(List.of());
        when(comisionesRepository.findByProductoIdInAndDeletedFalse(any())).thenReturn(List.of());
        when(ventasRepository.sumarVentasPorEmpleadoYProducto(any(), any(), any())).thenReturn(List.of());

        assertTrue(service.generar(filtro(inicio, fin, productoId)).isEmpty());
    }

    @Test
    void rechazaCuandoFaltaElProducto() {
        LocalDateTime inicio = LocalDateTime.of(2026, 10, 1, 0, 0);
        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> service.generar(ReporteVentasEmpleadosMetasRequestDTO.builder()
                        .fechaInicio(inicio)
                        .fechaFin(inicio.plusDays(1))
                        .build()));

        assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());
        verifyNoInteractions(ventasRepository);
    }

    private static ReporteVentasEmpleadosMetasRequestDTO filtro(LocalDateTime inicio, LocalDateTime fin, UUID productoId) {
        return ReporteVentasEmpleadosMetasRequestDTO.builder()
                .fechaInicio(inicio)
                .fechaFin(fin)
                .idProductos(List.of(productoId))
                .build();
    }

    private static MetaVentaEmpleadoEntity meta(UUID empleadoId,
                                                String nombre,
                                                String apellido,
                                                UUID productoId,
                                                String producto,
                                                String alias,
                                                String minima,
                                                boolean activa,
                                                LocalDateTime fechaInicio) {
        EmpleadosEntity empleado = new EmpleadosEntity();
        empleado.setId(empleadoId);
        empleado.setNombre(nombre);
        empleado.setPrimerApellido(apellido);
        ProductoComercialEntity productoEntity = new ProductoComercialEntity();
        productoEntity.setId(productoId);
        productoEntity.setNombre(producto);
        productoEntity.setAlias(alias);
        MetaVentaEmpleadoEntity meta = new MetaVentaEmpleadoEntity();
        meta.setEmpleado(empleado);
        meta.setProducto(productoEntity);
        meta.setCantidadMinima(new BigDecimal(minima));
        meta.setActiva(activa);
        meta.setFechaInicio(fechaInicio);
        return meta;
    }

    private static TablasComisionesEntity tabla(UUID productoId, String comision) {
        ProductoComercialEntity producto = new ProductoComercialEntity();
        producto.setId(productoId);
        TablasComisionesEntity tabla = new TablasComisionesEntity();
        tabla.setProducto(producto);
        tabla.setComision(new BigDecimal(comision));
        tabla.setDeleted(false);
        return tabla;
    }

    private static VentaEmpleadoProductoAgg venta(UUID empleadoId,
                                                  String nombre,
                                                  String apellido,
                                                  UUID productoId,
                                                  String producto,
                                                  String alias,
                                                  String cantidad) {
        return new VentaEmpleadoProductoAgg(
                empleadoId, nombre, apellido, null, productoId, producto, alias, new BigDecimal(cantidad));
    }
}
