package com.main.tancol.tablasamonestaciones.service;

import com.main.tancol.common.exeptions.IDNotFound;
import com.main.tancol.common.exceptions.exeption.PropiedaDuplicada;
import com.main.tancol.producto.entities.comerciales.ProductoComercialEntity;
import com.main.tancol.producto.repositories.ProductoComercialRepository;
import com.main.tancol.tablasamonestaciones.dto.request.TablasAmonestacionesRequestDTO;
import com.main.tancol.tablasamonestaciones.dto.response.ProductoAmonestacionDisponibleResponseDTO;
import com.main.tancol.tablasamonestaciones.dto.response.TablasAmonestacionesResponseDTO;
import com.main.tancol.tablasamonestaciones.entities.TablasAmonestacionesEntity;
import com.main.tancol.tablasamonestaciones.repositories.TablasAmonestacionesRepository;
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
class TablasAmonestacionesServiceTest {

    @Mock
    private TablasAmonestacionesRepository repository;

    @Mock
    private ProductoComercialRepository productoRepository;

    @InjectMocks
    private TablasAmonestacionesService service;

    @Test
    void createGuardaDescuentoNegativoYTextoDelProducto() {
        UUID productoId = UUID.randomUUID();
        ProductoComercialEntity producto = producto(productoId, "Garrafón", true, false);
        when(productoRepository.findById(productoId)).thenReturn(Optional.of(producto));
        when(repository.existsByProducto_IdAndDeletedFalse(productoId)).thenReturn(false);
        when(repository.save(any(TablasAmonestacionesEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TablasAmonestacionesResponseDTO response = service.create(request(1, 5, productoId, "-12.50"));

        assertEquals(0, new BigDecimal("-12.50").compareTo(response.getDescuento()));
        assertEquals("Por cada Garrafón roto", response.getTextoDescuento());
        assertEquals(1, response.getCantidadDesde());
        assertEquals(5, response.getCantidadHasta());
        assertEquals(productoId, response.getProductoId());
    }

    @Test
    void createRechazaCuandoAEsMenorQueDe() {
        assertThrows(PropiedaDuplicada.class, () -> service.create(request(10, 4, UUID.randomUUID(), "-1.00")));
        verify(repository, never()).save(any());
    }

    @Test
    void createRechazaDescuentoPositivo() {
        assertThrows(PropiedaDuplicada.class, () -> service.create(request(1, 2, UUID.randomUUID(), "1.00")));
        verify(productoRepository, never()).findById(any());
    }

    @Test
    void createRechazaProductoYaConfigurado() {
        UUID productoId = UUID.randomUUID();
        when(productoRepository.findById(productoId)).thenReturn(Optional.of(producto(productoId, "Agua", true, false)));
        when(repository.existsByProducto_IdAndDeletedFalse(productoId)).thenReturn(true);

        assertThrows(PropiedaDuplicada.class, () -> service.create(request(1, 5, productoId, "0.00")));
        verify(repository, never()).save(any());
    }

    @Test
    void createRechazaProductoInactivo() {
        UUID productoId = UUID.randomUUID();
        when(productoRepository.findById(productoId)).thenReturn(Optional.of(producto(productoId, "Agua", false, false)));

        assertThrows(IDNotFound.class, () -> service.create(request(1, 5, productoId, "0.00")));
    }

    @Test
    void productosDisponiblesRespetaElListadoDelRepositorio() {
        ProductoComercialEntity producto = producto(UUID.randomUUID(), "Hielo", true, false);
        when(repository.findProductosDisponibles(null)).thenReturn(List.of(producto));

        List<ProductoAmonestacionDisponibleResponseDTO> disponibles = service.productosDisponibles(null);

        assertEquals(1, disponibles.size());
        assertEquals("Hielo", disponibles.get(0).getNombre());
        assertEquals(producto.getId(), disponibles.get(0).getIdProducto());
    }

    @Test
    void deleteLiberaElProducto() {
        UUID id = UUID.randomUUID();
        TablasAmonestacionesEntity entity = TablasAmonestacionesEntity.builder()
                .id(id)
                .cantidadDesde(1)
                .cantidadHasta(3)
                .descuento(new BigDecimal("-1.00"))
                .producto(producto(UUID.randomUUID(), "Agua", true, false))
                .build();
        when(repository.findById(id)).thenReturn(Optional.of(entity));
        when(repository.save(entity)).thenReturn(entity);

        service.delete(id);

        assertTrue(entity.isDeleted());
    }

    private TablasAmonestacionesRequestDTO request(int desde, int hasta, UUID productoId, String descuento) {
        return new TablasAmonestacionesRequestDTO(desde, hasta, productoId, new BigDecimal(descuento));
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
