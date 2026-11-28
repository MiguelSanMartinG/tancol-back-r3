package com.main.tancol.cuadreinventario.service;

import com.main.tancol.almacen.entities.AlmacenEntity;
import com.main.tancol.almacen.entities.InventarioEntity;
import com.main.tancol.almacen.repositories.AlmacenRepository;
import com.main.tancol.almacen.repositories.InventarioRepository;
import com.main.tancol.common.exceptions.exeption.GenericExeption;
import com.main.tancol.cuadreinventario.dto.CuadreInventarioDTOs.CapturaPartida;
import com.main.tancol.cuadreinventario.dto.CuadreInventarioDTOs.CrearRequest;
import com.main.tancol.cuadreinventario.dto.CuadreInventarioDTOs.PartidaResponse;
import com.main.tancol.cuadreinventario.dto.CuadreInventarioDTOs.Response;
import com.main.tancol.cuadreinventario.entities.CuadreInventarioEntity;
import com.main.tancol.cuadreinventario.entities.CuadreInventarioPartidaEntity;
import com.main.tancol.cuadreinventario.enums.EstadoCuadre;
import com.main.tancol.cuadreinventario.enums.TipoCuadre;
import com.main.tancol.cuadreinventario.repositories.CuadreInventarioRepository;
import com.main.tancol.producto.entities.insumos.CategoriaProductoEntity;
import com.main.tancol.producto.entities.insumos.ProductoBasicoEntity;
import com.main.tancol.producto.repositories.CategoriaProductoRepository;
import com.main.tancol.producto.service.insumo.StockMinimoRevisionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CuadreInventarioServiceTest {

    private static CuadreInventarioEntity cuadreConDosPartidas() {
        AlmacenEntity almacen = new AlmacenEntity();
        almacen.setId(UUID.randomUUID());
        almacen.setNombre("Central");
        CuadreInventarioEntity cuadre = CuadreInventarioEntity.builder().almacen(almacen).build();
        cuadre.getPartidas().add(CuadreInventarioPartidaEntity.builder().id(UUID.randomUUID()).cuadre(cuadre)
                .producto("Garrafón").cantidadSistema(new BigDecimal("10")).build());
        cuadre.getPartidas().add(CuadreInventarioPartidaEntity.builder().id(UUID.randomUUID()).cuadre(cuadre)
                .producto("Tapa").cantidadSistema(new BigDecimal("5")).build());
        return cuadre;
    }

    private static CapturaPartida captura(UUID id, String cantidad) {
        CapturaPartida c = new CapturaPartida();
        c.setIdPartida(id);
        c.setCantidadFisica(cantidad == null ? null : new BigDecimal(cantidad));
        return c;
    }

    @Test
    void captura_calculaDiferenciaPositivaYNegativa() {
        CuadreInventarioEntity cuadre = cuadreConDosPartidas();
        UUID a = cuadre.getPartidas().get(0).getId();
        UUID b = cuadre.getPartidas().get(1).getId();
        CuadreInventarioService.aplicarCaptura(cuadre, List.of(captura(a, "12"), captura(b, "3")), "  ");

        assertEquals(new BigDecimal("2"), cuadre.getPartidas().get(0).getDiferencia());
        assertEquals(new BigDecimal("-2"), cuadre.getPartidas().get(1).getDiferencia());
        assertNull(cuadre.getObservaciones());

        Response r = CuadreInventarioService.toResponse(cuadre, true);
        assertEquals(2, r.getPartidasCapturadas());
        assertEquals(2, r.getPartidasConDiferencia());
        assertEquals(2, r.getPartidas().size());
    }

    @Test
    void captura_sinCantidad_quedaPendiente_y_sinDiferencia() {
        CuadreInventarioEntity cuadre = cuadreConDosPartidas();
        UUID a = cuadre.getPartidas().get(0).getId();
        CuadreInventarioService.aplicarCaptura(cuadre, List.of(captura(a, "10")), "Conteo de la mañana");
        Response r = CuadreInventarioService.toResponse(cuadre, false);
        assertEquals(1, r.getPartidasCapturadas());
        assertEquals(0, r.getPartidasConDiferencia());
        assertNull(r.getPartidas());
        assertEquals("Conteo de la mañana", cuadre.getObservaciones());
    }

    @Test
    void captura_negativa_o_de_otro_cuadre_falla() {
        CuadreInventarioEntity cuadre = cuadreConDosPartidas();
        UUID a = cuadre.getPartidas().get(0).getId();
        assertThrows(GenericExeption.class,
                () -> CuadreInventarioService.aplicarCaptura(cuadre, List.of(captura(a, "-1")), null));
        assertThrows(GenericExeption.class,
                () -> CuadreInventarioService.aplicarCaptura(cuadre, List.of(captura(UUID.randomUUID(), "1")), null));
    }

    // ---------------------------------------------------------------- tipo y categorías (Buzón 10)

    @Mock
    private CuadreInventarioRepository repository;
    @Mock
    private AlmacenRepository almacenRepository;
    @Mock
    private InventarioRepository inventarioRepository;
    @Mock
    private StockMinimoRevisionService stockMinimoRevisionService;
    @Mock
    private CategoriaProductoRepository categoriaProductoRepository;
    @InjectMocks
    private CuadreInventarioService service;

    private static CategoriaProductoEntity categoria(String nombre, CategoriaProductoEntity padre) {
        CategoriaProductoEntity c = new CategoriaProductoEntity();
        c.setId(UUID.randomUUID());
        c.setNombre(nombre);
        c.setCategoriaPadre(padre);
        return c;
    }

    private static InventarioEntity item(AlmacenEntity almacen, String producto, CategoriaProductoEntity categoria) {
        ProductoBasicoEntity p = new ProductoBasicoEntity();
        p.setId(UUID.randomUUID());
        p.setNombre(producto);
        p.setCategoriaProductoEntity(categoria);
        InventarioEntity i = new InventarioEntity();
        i.setId(UUID.randomUUID());
        i.setAlmacen(almacen);
        i.setProductoBasicoEntity(p);
        i.setCantidad(BigDecimal.TEN);
        return i;
    }

    private static CrearRequest request(UUID idAlmacen, String tipo, List<UUID> idsCategoria) {
        CrearRequest r = new CrearRequest();
        r.setIdAlmacen(idAlmacen);
        r.setTipo(tipo);
        r.setIdsCategoria(idsCategoria);
        return r;
    }

    private AlmacenEntity almacenSinCuadreAbierto() {
        AlmacenEntity almacen = new AlmacenEntity();
        almacen.setId(UUID.randomUUID());
        almacen.setNombre("Bodega San Antonio");
        when(almacenRepository.findById(almacen.getId())).thenReturn(Optional.of(almacen));
        when(repository.existsByAlmacen_IdAndEstadoAndDeletedFalse(almacen.getId(), EstadoCuadre.ABIERTO))
                .thenReturn(false);
        return almacen;
    }

    @SuppressWarnings("unchecked")
    private void conInventario(List<InventarioEntity> inventario) {
        when(inventarioRepository.findAll(any(Specification.class))).thenReturn(inventario);
    }

    private void guardaSinCambios() {
        when(repository.siguienteFolio()).thenReturn(7L);
        when(repository.save(any(CuadreInventarioEntity.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void crear_conCategorias_soloTomaEsasYSusSubcategorias() {
        AlmacenEntity almacen = almacenSinCuadreAbierto();
        CategoriaProductoEntity barnices = categoria("Barnices", null);
        CategoriaProductoEntity mate = categoria("Barnices mate", barnices);
        CategoriaProductoEntity mateFino = categoria("Mate fino", mate);
        CategoriaProductoEntity electricos = categoria("Componentes eléctricos", null);
        when(categoriaProductoRepository.findAll()).thenReturn(List.of(barnices, mate, mateFino, electricos));
        conInventario(List.of(
                item(almacen, "Barniz brillante", barnices),
                item(almacen, "Barniz mate", mate),
                item(almacen, "Barniz mate fino", mateFino),
                item(almacen, "Cable", electricos),
                item(almacen, "Sin categoría", null)));
        guardaSinCambios();

        Response r = service.crear(request(almacen.getId(), "mensual", List.of(barnices.getId())));

        assertEquals(TipoCuadre.MENSUAL, r.getTipo());
        assertEquals("CI-00007", r.getFolio());
        assertEquals(List.of("Barniz brillante", "Barniz mate", "Barniz mate fino"),
                r.getPartidas().stream().map(PartidaResponse::getProducto).toList());
        assertEquals(1, r.getCategorias().size());
        assertEquals(barnices.getId(), r.getCategorias().get(0).getId());
        assertEquals("Barnices", r.getCategorias().get(0).getNombre());
    }

    @Test
    void crear_sinCategorias_tomaTodoElAlmacen() {
        AlmacenEntity almacen = almacenSinCuadreAbierto();
        CategoriaProductoEntity barnices = categoria("Barnices", null);
        conInventario(List.of(
                item(almacen, "Barniz brillante", barnices),
                item(almacen, "Sin categoría", null)));
        guardaSinCambios();

        Response vacio = service.crear(request(almacen.getId(), "DIARIO", List.of()));

        assertEquals(TipoCuadre.DIARIO, vacio.getTipo());
        assertEquals(2, vacio.getTotalPartidas());
        assertTrue(vacio.getCategorias().isEmpty());
        verifyNoInteractions(categoriaProductoRepository);
    }

    @Test
    void crear_conCategoriasSinInsumosEnElAlmacen_falla() {
        AlmacenEntity almacen = almacenSinCuadreAbierto();
        CategoriaProductoEntity barnices = categoria("Barnices", null);
        CategoriaProductoEntity electricos = categoria("Componentes eléctricos", null);
        when(categoriaProductoRepository.findAll()).thenReturn(List.of(barnices, electricos));
        conInventario(List.of(item(almacen, "Barniz brillante", barnices)));

        GenericExeption ex = assertThrows(GenericExeption.class,
                () -> service.crear(request(almacen.getId(), "ANUAL", List.of(electricos.getId()))));

        assertEquals("El almacén no tiene insumos de las categorías elegidas", ex.getMessage());
        verify(repository, never()).save(any(CuadreInventarioEntity.class));
    }

    @Test
    void crear_sinTipoOTipoInvalido_falla() {
        UUID idAlmacen = UUID.randomUUID();
        assertThrows(GenericExeption.class, () -> service.crear(request(idAlmacen, null, List.of())));
        assertThrows(GenericExeption.class, () -> service.crear(request(idAlmacen, "  ", List.of())));
        GenericExeption ex = assertThrows(GenericExeption.class,
                () -> service.crear(request(idAlmacen, "QUINCENAL", List.of())));
        assertTrue(ex.getMessage().contains("QUINCENAL"));
        verifyNoInteractions(almacenRepository, repository, inventarioRepository);
    }

    @Test
    void response_deCuadreAnterior_sinTipoYSinCategorias() {
        Response r = CuadreInventarioService.toResponse(cuadreConDosPartidas(), false);
        assertNull(r.getTipo());
        assertTrue(r.getCategorias().isEmpty());
    }
}
