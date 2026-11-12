package com.main.tancol.tablascomisiones.service;

import com.main.tancol.common.exeptions.IDNotFound;
import com.main.tancol.common.exceptions.exeption.PropiedaDuplicada;
import com.main.tancol.producto.entities.comerciales.ProductoComercialEntity;
import com.main.tancol.producto.repositories.ProductoComercialRepository;
import com.main.tancol.tablascomisiones.dto.request.TablasComisionesRequestDTO;
import com.main.tancol.tablascomisiones.dto.response.ProductoComisionDisponibleResponseDTO;
import com.main.tancol.tablascomisiones.dto.response.TablasComisionesResponseDTO;
import com.main.tancol.tablascomisiones.entities.TablasComisionesEntity;
import com.main.tancol.tablascomisiones.repositories.TablasComisionesRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TablasComisionesServiceTest {

    @Mock
    private TablasComisionesRepository repository;

    @Mock
    private ProductoComercialRepository productoRepository;

    @InjectMocks
    private TablasComisionesService service;

    @Test
    void createGuardaComisionEnCeroYTextoDelProducto() {
        UUID productoId = UUID.randomUUID();
        ProductoComercialEntity producto = producto(productoId, "Garrafón", true, false);
        when(productoRepository.findById(productoId)).thenReturn(Optional.of(producto));
        when(repository.existsByProducto_IdAndDeletedFalse(productoId)).thenReturn(false);
        when(repository.save(any(TablasComisionesEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TablasComisionesResponseDTO response = service.create(request(10, 10, 20, productoId, "0"));

        assertEquals(0, new BigDecimal("0.00").compareTo(response.getComision()));
        assertEquals("Por cada Garrafón vendido", response.getTextoComision());
        assertEquals(10, response.getVentaMinima());
        assertEquals(10, response.getCantidadDesde());
        assertEquals(20, response.getCantidadHasta());
        assertEquals(productoId, response.getProductoId());
    }

    @Test
    void createRechazaCuandoAEsMenorQueDe() {
        assertThrows(PropiedaDuplicada.class, () -> service.create(request(5, 10, 4, UUID.randomUUID(), "1.50")));
        verify(repository, never()).save(any());
    }

    @Test
    void createRechazaComisionConMasDeDosDecimales() {
        assertThrows(PropiedaDuplicada.class, () -> service.create(request(1, 1, 2, UUID.randomUUID(), "1.005")));
        verify(productoRepository, never()).findById(any());
    }

    @Test
    void createRechazaProductoYaConfigurado() {
        UUID productoId = UUID.randomUUID();
        when(productoRepository.findById(productoId)).thenReturn(Optional.of(producto(productoId, "Agua", true, false)));
        when(repository.existsByProducto_IdAndDeletedFalse(productoId)).thenReturn(true);

        assertThrows(PropiedaDuplicada.class, () -> service.create(request(1, 1, 5, productoId, "0.00")));
        verify(repository, never()).save(any());
    }

    @Test
    void createRechazaProductoInactivo() {
        UUID productoId = UUID.randomUUID();
        when(productoRepository.findById(productoId)).thenReturn(Optional.of(producto(productoId, "Agua", false, false)));

        assertThrows(IDNotFound.class, () -> service.create(request(1, 1, 5, productoId, "0.00")));
    }

    @Test
    void deleteLiberaElProducto() {
        UUID id = UUID.randomUUID();
        TablasComisionesEntity entity = TablasComisionesEntity.builder()
                .id(id)
                .ventaMinima(1)
                .cantidadDesde(1)
                .cantidadHasta(3)
                .comision(new BigDecimal("1.00"))
                .producto(producto(UUID.randomUUID(), "Agua", true, false))
                .build();
        when(repository.findById(id)).thenReturn(Optional.of(entity));
        when(repository.save(entity)).thenReturn(entity);

        service.delete(id);

        assertTrue(entity.isDeleted());
    }

    @Test
    void productosDisponiblesRespetaElListadoDelRepositorio() {
        ProductoComercialEntity producto = producto(UUID.randomUUID(), "Hielo", true, false);
        when(repository.findProductosDisponibles(null)).thenReturn(List.of(producto));

        List<ProductoComisionDisponibleResponseDTO> disponibles = service.productosDisponibles(null);

        assertEquals(1, disponibles.size());
        assertEquals("Hielo", disponibles.get(0).getNombre());
        assertEquals(producto.getId(), disponibles.get(0).getIdProducto());
    }

    private TablasComisionesRequestDTO request(int ventaMinima, int desde, int hasta, UUID productoId, String comision) {
        return new TablasComisionesRequestDTO(ventaMinima, desde, hasta, productoId, new BigDecimal(comision));
    }

    private ProductoComercialEntity producto(UUID id, String nombre, boolean enabled, boolean deleted) {
        ProductoComercialEntity producto = ProductoComercialEntity.builder()
                .id(id)
                .nombre(nombre)
                .alias(nombre)
                .build();
        producto.setEnabled(enabled);
        producto.setDeleted(deleted);
        return producto;
    }
}
