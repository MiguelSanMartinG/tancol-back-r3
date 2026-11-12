package com.main.tancol.almacen.service;

import com.main.tancol.almacen.dto.request.InventarioRequestDTO;
import com.main.tancol.almacen.dto.request.partidas.PartidaTransferenciaRequestDTO;
import com.main.tancol.almacen.dto.response.InventarioResponseDTO;
import com.main.tancol.almacen.entities.AlmacenEntity;
import com.main.tancol.almacen.entities.InventarioEntity;
import com.main.tancol.almacen.repositories.AlmacenRepository;
import com.main.tancol.almacen.repositories.InventarioRepository;
import com.main.tancol.almacen.repositories.UnidadesMedidaRepository;
import com.main.tancol.common.exeptions.IDNotFound;
import com.main.tancol.empleados.entities.EmpleadosEntity;
import com.main.tancol.producto.entities.insumos.ProductoBasicoEntity;
import com.main.tancol.producto.repositories.ProductoRepository;
import com.main.tancol.producto.service.insumo.PrecioService;
import com.main.tancol.producto.service.insumo.StockMinimoRevisionService;
import com.main.tancol.proveedor.repositories.ProveedorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventarioServiceTest {

    @Mock
    private InventarioRepository inventarioRepository;
    @Mock
    private AlmacenRepository almacenRepository;
    @Mock
    private ProductoRepository productoRepository;
    @Mock
    private UnidadesMedidaRepository unidadDeMedidaRepository;
    @Mock
    private ProveedorRepository proveedorRepository;
    @Mock
    private PrecioService precioService;
    @Mock
    private StockMinimoRevisionService stockMinimoRevisionService;

    @InjectMocks
    private InventarioService inventarioService;

    private UUID idAlmacen;
    private UUID idProducto;
    private AlmacenEntity almacen;
    private ProductoBasicoEntity producto;

    @BeforeEach
    void setUp() {
        idAlmacen = UUID.randomUUID();
        idProducto = UUID.randomUUID();
        almacen = AlmacenEntity.builder().id(idAlmacen).nombre("Almacen A").build();
        producto = ProductoBasicoEntity.builder().id(idProducto).nombre("Diesel").build();
    }

    @Test
    void createSumaCantidadSiYaExisteElProductoEnElAlmacen() {
        InventarioEntity existente = InventarioEntity.builder()
                .id(UUID.randomUUID())
                .almacen(almacen)
                .productoBasicoEntity(producto)
                .cantidad(new BigDecimal("10"))
                .build();

        when(almacenRepository.findById(idAlmacen)).thenReturn(Optional.of(almacen));
        when(productoRepository.findById(idProducto)).thenReturn(Optional.of(producto));
        when(inventarioRepository.findByAlmacen_IdAndProductoBasicoEntity_Id(idAlmacen, idProducto))
                .thenReturn(Optional.of(existente));
        when(inventarioRepository.save(any(InventarioEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        InventarioRequestDTO dto = new InventarioRequestDTO(idAlmacen, idProducto, new BigDecimal("5"), null, null, null);
        InventarioResponseDTO response = inventarioService.create(dto);

        assertEquals(new BigDecimal("15"), response.getCantidad());
        verify(inventarioRepository, times(1)).save(existente);
    }

    @Test
    void createRechazaCantidadNegativa() {
        when(almacenRepository.findById(idAlmacen)).thenReturn(Optional.of(almacen));
        when(productoRepository.findById(idProducto)).thenReturn(Optional.of(producto));
        when(inventarioRepository.findByAlmacen_IdAndProductoBasicoEntity_Id(idAlmacen, idProducto))
                .thenReturn(Optional.empty());

        InventarioRequestDTO dto = new InventarioRequestDTO(idAlmacen, idProducto, new BigDecimal("-1"), null, null, null);
        assertThrows(IDNotFound.class, () -> inventarioService.create(dto));
        verify(inventarioRepository, never()).save(any());
    }

    @Test
    void transferirMueveStockEntreAlmacenes() {
        AlmacenEntity destino = AlmacenEntity.builder().id(UUID.randomUUID()).nombre("Almacen B").build();
        InventarioEntity origenInv = InventarioEntity.builder()
                .id(UUID.randomUUID())
                .almacen(almacen)
                .productoBasicoEntity(producto)
                .cantidad(new BigDecimal("20"))
                .build();
        InventarioEntity destinoInv = InventarioEntity.builder()
                .id(UUID.randomUUID())
                .almacen(destino)
                .productoBasicoEntity(producto)
                .cantidad(new BigDecimal("3"))
                .build();

        when(inventarioRepository.findByAlmacen_IdAndProductoBasicoEntity_Id(almacen.getId(), idProducto))
                .thenReturn(Optional.of(origenInv));
        when(inventarioRepository.findByAlmacen_IdAndProductoBasicoEntity_Id(destino.getId(), idProducto))
                .thenReturn(Optional.of(destinoInv));
        when(inventarioRepository.save(any(InventarioEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PartidaTransferenciaRequestDTO dto = new PartidaTransferenciaRequestDTO(
                1, almacen.getId(), destino.getId(), idProducto, new BigDecimal("8"), null, null);

        inventarioService.transferir(dto, almacen, destino, producto, new EmpleadosEntity(), null);

        assertEquals(new BigDecimal("12"), origenInv.getCantidad());
        assertEquals(new BigDecimal("11"), destinoInv.getCantidad());
        verify(inventarioRepository, times(2)).save(any(InventarioEntity.class));
    }

    @Test
    void transferirCreaInventarioEnDestinoSiNoExiste() {
        AlmacenEntity destino = AlmacenEntity.builder().id(UUID.randomUUID()).nombre("Almacen B").build();
        InventarioEntity origenInv = InventarioEntity.builder()
                .id(UUID.randomUUID())
                .almacen(almacen)
                .productoBasicoEntity(producto)
                .cantidad(new BigDecimal("20"))
                .build();

        when(inventarioRepository.findByAlmacen_IdAndProductoBasicoEntity_Id(almacen.getId(), idProducto))
                .thenReturn(Optional.of(origenInv));
        when(inventarioRepository.findByAlmacen_IdAndProductoBasicoEntity_Id(destino.getId(), idProducto))
                .thenReturn(Optional.empty());
        when(inventarioRepository.save(any(InventarioEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PartidaTransferenciaRequestDTO dto = new PartidaTransferenciaRequestDTO(
                1, almacen.getId(), destino.getId(), idProducto, new BigDecimal("4"), null, null);

        inventarioService.transferir(dto, almacen, destino, producto, new EmpleadosEntity(), null);

        ArgumentCaptor<InventarioEntity> captor = ArgumentCaptor.forClass(InventarioEntity.class);
        verify(inventarioRepository, times(2)).save(captor.capture());
        InventarioEntity creado = captor.getAllValues().stream()
                .filter(entity -> destino.equals(entity.getAlmacen()))
                .findFirst()
                .orElseThrow();
        assertEquals(new BigDecimal("4"), creado.getCantidad());
        assertEquals(new BigDecimal("16"), origenInv.getCantidad());
    }

    @Test
    void transferirFallaSiNoHayExistenciaSuficiente() {
        AlmacenEntity destino = AlmacenEntity.builder().id(UUID.randomUUID()).nombre("Almacen B").build();
        InventarioEntity origenInv = InventarioEntity.builder()
                .id(UUID.randomUUID())
                .almacen(almacen)
                .productoBasicoEntity(producto)
                .cantidad(new BigDecimal("2"))
                .build();

        when(inventarioRepository.findByAlmacen_IdAndProductoBasicoEntity_Id(almacen.getId(), idProducto))
                .thenReturn(Optional.of(origenInv));
        when(inventarioRepository.findByAlmacen_IdAndProductoBasicoEntity_Id(destino.getId(), idProducto))
                .thenReturn(Optional.empty());

        PartidaTransferenciaRequestDTO dto = new PartidaTransferenciaRequestDTO(
                1, almacen.getId(), destino.getId(), idProducto, new BigDecimal("5"), null, null);

        assertThrows(IDNotFound.class, () ->
                inventarioService.transferir(dto, almacen, destino, producto, new EmpleadosEntity(), null));
        verify(inventarioRepository, never()).save(any());
    }

    @Test
    void transferirFallaSiOrigenYDestinoSonElMismoAlmacen() {
        PartidaTransferenciaRequestDTO dto = new PartidaTransferenciaRequestDTO(
                1, almacen.getId(), almacen.getId(), idProducto, new BigDecimal("1"), null, null);

        assertThrows(IDNotFound.class, () ->
                inventarioService.transferir(dto, almacen, almacen, producto, new EmpleadosEntity(), null));
    }

    @Test
    void deleteNoVuelveAGuardarElRegistro() {
        InventarioEntity entity = InventarioEntity.builder()
                .id(UUID.randomUUID())
                .almacen(almacen)
                .productoBasicoEntity(producto)
                .cantidad(new BigDecimal("9"))
                .build();
        when(inventarioRepository.findById(entity.getId())).thenReturn(Optional.of(entity));

        inventarioService.delete(entity.getId());

        verify(inventarioRepository).delete(entity);
        verify(inventarioRepository, never()).save(any());
    }
}
