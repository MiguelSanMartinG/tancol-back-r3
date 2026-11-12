package com.main.tancol.dashboard.query;

import com.main.tancol.almacen.entities.OrdenInternaEntity;
import com.main.tancol.almacen.entities.UnidadesMedidaEntity;
import com.main.tancol.almacen.entities.partidas.PartidaOrdenInternaEntity;
import com.main.tancol.almacen.repositories.PartidaOrdenInternaRepository;
import com.main.tancol.dashboard.dto.response.TopDepartamentoInsumosResponseDTO;
import com.main.tancol.dashboard.dto.response.TopDepartamentoInsumosResponseDTO.Producto;
import com.main.tancol.dashboard.dto.response.TopInsumoCompradoResponseDTO;
import com.main.tancol.dashboard.util.DashboardPeriodo;
import com.main.tancol.orden.entities.PartidaOrdenEntity;
import com.main.tancol.orden.repositories.PartidaOrdenRepository;
import com.main.tancol.producto.entities.insumos.ProductoBasicoEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardInsumosQueryTest {

    @Mock
    private PartidaOrdenInternaRepository partidaOrdenInternaRepository;

    @Mock
    private PartidaOrdenRepository partidaOrdenRepository;

    @InjectMocks
    private DashboardInsumosQuery query;

    @Test
    void devuelveTop10DepartamentosYTop3ProductosPorCantidad() {
        List<PartidaOrdenInternaEntity> partidas = new ArrayList<>();

        ProductoBasicoEntity cable = producto("Cable TPU");
        ProductoBasicoEntity mouse = producto("Mouse Logitec");
        ProductoBasicoEntity hojas = producto("Hojas carta");
        ProductoBasicoEntity extra = producto("Toner");
        partidas.add(partida("TI", cable, new BigDecimal("60"), "paquetes"));
        partidas.add(partida("ti", cable, new BigDecimal("40"), "paquetes"));
        partidas.add(partida("TI", mouse, new BigDecimal("5"), "pz"));
        partidas.add(partida("TI", hojas, new BigDecimal("10"), "pz"));
        partidas.add(partida("TI", extra, new BigDecimal("1"), "pz"));

        partidas.add(partida("Limpieza", producto("Cloralex"), new BigDecimal("600"), "L"));
        partidas.add(partida("Limpieza", producto("Papel de baño"), new BigDecimal("50"), "paquetes"));
        partidas.add(partida("Limpieza", producto("Escobas"), new BigDecimal("40"), "pz"));

        for (int i = 1; i <= 9; i++) {
            partidas.add(partida("Depto " + i, producto("Insumo " + i), new BigDecimal(30 - i), "pz"));
        }

        when(partidaOrdenInternaRepository.findAll(any(Specification.class))).thenReturn(partidas);

        List<TopDepartamentoInsumosResponseDTO> departamentos = query.topDepartamentos(
                DashboardPeriodo.of(LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 30)), 10);

        assertEquals(10, departamentos.size());
        assertTrue(departamentos.stream().noneMatch(d -> "Depto 9".equals(d.getDepartamento())));

        TopDepartamentoInsumosResponseDTO limpieza = departamentos.get(0);
        assertEquals("Limpieza", limpieza.getDepartamento());
        assertEquals(0, new BigDecimal("690").compareTo(limpieza.getCantidadTotal()));
        assertEquals(List.of("Cloralex", "Papel de baño", "Escobas"),
                limpieza.getProductos().stream().map(Producto::getProducto).toList());
        assertEquals("L", limpieza.getProductos().get(0).getUnidad());

        TopDepartamentoInsumosResponseDTO ti = departamentos.stream()
                .filter(d -> "TI".equals(d.getDepartamento()))
                .findFirst()
                .orElseThrow();
        assertEquals(0, new BigDecimal("116").compareTo(ti.getCantidadTotal()));
        assertEquals(3, ti.getProductos().size());
        assertEquals("Cable TPU", ti.getProductos().get(0).getProducto());
        assertEquals(0, new BigDecimal("100").compareTo(ti.getProductos().get(0).getCantidad()));
        assertEquals("paquetes", ti.getProductos().get(0).getUnidad());
        assertEquals(List.of("Cable TPU", "Hojas carta", "Mouse Logitec"),
                ti.getProductos().stream().map(Producto::getProducto).toList());
    }

    @Test
    void devuelveTop10InsumosCompradosSumandoLaMismaUnidad() {
        ProductoBasicoEntity cable = producto("Cable TPU");
        List<PartidaOrdenEntity> partidas = new ArrayList<>();
        partidas.add(compra(cable, 60, "paquetes"));
        partidas.add(compra(cable, 40, "paquetes"));
        partidas.add(compra(cable, 5, "pz"));
        for (int i = 1; i <= 10; i++) {
            partidas.add(compra(producto("Insumo " + i), 30 - i, "pz"));
        }

        when(partidaOrdenRepository.findAll(any(Specification.class))).thenReturn(partidas);

        List<TopInsumoCompradoResponseDTO> top = query.topInsumosComprados(
                DashboardPeriodo.of(LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 30)), 10);

        assertEquals(10, top.size());
        assertEquals("Cable TPU", top.get(0).getProducto());
        assertEquals(0, new BigDecimal("100").compareTo(top.get(0).getCantidad()));
        assertEquals("paquetes", top.get(0).getUnidad());
        assertTrue(top.stream().noneMatch(item -> "Insumo 10".equals(item.getProducto())));
        assertTrue(top.stream().noneMatch(item -> "pz".equals(item.getUnidad()) && "Cable TPU".equals(item.getProducto())));
    }

    private static ProductoBasicoEntity producto(String nombre) {
        return ProductoBasicoEntity.builder()
                .id(UUID.randomUUID())
                .nombre(nombre)
                .build();
    }

    private static PartidaOrdenEntity compra(ProductoBasicoEntity producto, int cantidad, String unidad) {
        return PartidaOrdenEntity.builder()
                .producto(producto)
                .cantidad(cantidad)
                .unidadesMedida(UnidadesMedidaEntity.builder().alias(unidad).build())
                .build();
    }

    private static PartidaOrdenInternaEntity partida(String uso, ProductoBasicoEntity producto, BigDecimal cantidad, String unidad) {
        return PartidaOrdenInternaEntity.builder()
                .ordenInternaEntity(OrdenInternaEntity.builder().uso(uso).build())
                .productoBasicoEntity(producto)
                .cantidad(cantidad)
                .unidadesMedidaEntity(UnidadesMedidaEntity.builder().alias(unidad).build())
                .build();
    }
}
