package com.main.tancol.producto.service.insumo;

import com.main.tancol.almacen.repositories.InventarioRepository;
import com.main.tancol.almacen.service.interfaces.ICategoriaProductoService;
import com.main.tancol.common.exceptions.exeption.GenericExeption;
import com.main.tancol.common.exeptions.IDNotFound;
import com.main.tancol.empleados.util.DepartamentoMapper;
import com.main.tancol.producto.SubcategoriaProductoBasico;
import com.main.tancol.producto.dto.request.insumo.CategoriaRequestDTO;
import com.main.tancol.producto.dto.response.insumos.CategoriaExistenciaProductoResponseDTO;
import com.main.tancol.producto.dto.response.insumos.CategoriaExistenciaResponseDTO;
import com.main.tancol.producto.dto.response.insumos.CategoriaProductoItemResponseDTO;
import com.main.tancol.producto.dto.response.insumos.CategoriaProductosResponseDTO;
import com.main.tancol.producto.dto.response.insumos.CategoriaRecuentoItemResponseDTO;
import com.main.tancol.producto.dto.response.insumos.CategoriaRecuentoResponseDTO;
import com.main.tancol.producto.dto.response.insumos.CategoriaResponseDTO;
import com.main.tancol.producto.dto.response.insumos.ExistenciaUnidadResponseDTO;
import com.main.tancol.producto.entities.insumos.CategoriaProductoEntity;
import com.main.tancol.producto.entities.insumos.ProductoBasicoEntity;
import com.main.tancol.producto.repositories.CategoriaProductoRepository;
import com.main.tancol.producto.repositories.ProductoRepository;
import com.main.tancol.producto.util.insumos.CateogriaSpecification;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

@Slf4j
@Service
public class CategoriaService implements ICategoriaProductoService {
    private static final String SIN_UNIDAD = "Sin unidad";

    private final CategoriaProductoRepository repository;
    private final ProductoRepository productoRepository;
    private final InventarioRepository inventarioRepository;

    public CategoriaService(CategoriaProductoRepository categoriaProductoRepository,
                            ProductoRepository productoRepository,
                            InventarioRepository inventarioRepository) {
        this.repository = categoriaProductoRepository;
        this.productoRepository = productoRepository;
        this.inventarioRepository = inventarioRepository;
    }

    @Override
    public CategoriaResponseDTO createCategoria(CategoriaRequestDTO dto) {
        if (!SubcategoriaProductoBasico.habilitada() && dto.getCategoriaPadreId() != null) {
            throw new GenericExeption("Las subcategorías de producto básico están deshabilitadas");
        }
        CategoriaProductoEntity padre = resolverPadre(dto.getCategoriaPadreId());
        CategoriaProductoEntity nwCategoria = CategoriaProductoEntity.builder()
                .nombre(dto.getNombre())
                .categoriaPadre(padre)
                .build();
        nwCategoria.setEnabled(true);

        return DepartamentoMapper.EntityToCategoriaProductoResponseDTO(repository.save(nwCategoria));
    }

    @Override
    @Transactional(readOnly = true)
    public CategoriaResponseDTO readByID(UUID id) {
        CategoriaProductoEntity busqueda = categoriaActiva(id);
        return DepartamentoMapper.EntityToCategoriaProductoResponseDTO(busqueda);
    }

    @Override
    @Operation(
            summary = "No se actualiza la lista"
    )
    public CategoriaResponseDTO updateCategoria(UUID id, CategoriaRequestDTO dto) {
        CategoriaProductoEntity categoria = repository.findById(id)
                .orElseThrow(() -> new IDNotFound("No se encontró el ID de la categoría ID buscada: " + id));
        categoria.setNombre(dto.getNombre());
        categoria.setEnabled(dto.getEnabled());
        if (SubcategoriaProductoBasico.habilitada()) {
            CategoriaProductoEntity padre = resolverPadre(dto.getCategoriaPadreId());
            validarPadre(categoria, padre);
            categoria.setCategoriaPadre(padre);
        } else if (dto.getCategoriaPadreId() != null) {
            throw new GenericExeption("Las subcategorías de producto básico están deshabilitadas");
        }
        CategoriaProductoEntity categoriaDB = repository.save(categoria);
        log.info("Categoría {} actualizada correctamente", categoriaDB.getId());

        return DepartamentoMapper.EntityToCategoriaProductoResponseDTO(categoriaDB);
    }

    @Override
    public CategoriaResponseDTO deleteCategoria(UUID id) {
        CategoriaProductoEntity busqueda = repository.findById(id).orElse(null);
        if (busqueda != null) {
            boolean tieneSubcategorias = busqueda.getSubcategorias() != null
                    && busqueda.getSubcategorias().stream().anyMatch(hija -> !hija.isDeleted());
            if (tieneSubcategorias) {
                throw new GenericExeption("No se puede eliminar una categoría que tiene subcategorías");
            }
            busqueda.setDeleted(true);
            busqueda.setProductoEntities(new ArrayList<>());
            return DepartamentoMapper.EntityToCategoriaProductoResponseDTO(repository.save(busqueda));
        }
        throw new IDNotFound("No se encontro el ID del almacen");
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaResponseDTO> getAll(Map<String, String> params) {
        List<CategoriaProductoEntity> list = repository.findAll(filterWithParameters(params));
        return list.stream().map(DepartamentoMapper::EntityToCategoriaProductoResponseDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoriaProductosResponseDTO productos(UUID id) {
        CategoriaProductoEntity categoria = categoriaActiva(id);
        List<ProductoBasicoEntity> productos = productosDirectos(id);
        return CategoriaProductosResponseDTO.builder()
                .idCategoria(categoria.getId())
                .nombre(categoria.getNombre())
                .numeroProductos(productos.size())
                .productos(productos.stream().map(this::aItem).toList())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoriaRecuentoResponseDTO recuento(UUID id) {
        if (!SubcategoriaProductoBasico.habilitada()) {
            throw new GenericExeption("Las subcategorías de producto básico están deshabilitadas");
        }
        CategoriaProductoEntity categoria = categoriaActiva(id);
        List<CategoriaProductoEntity> hijas = hijasDirectas(categoria);
        List<CategoriaRecuentoItemResponseDTO> filas = new ArrayList<>();
        for (CategoriaProductoEntity hija : hijas) {
            List<ProductoBasicoEntity> productos = productosEnRama(hija.getId());
            filas.add(CategoriaRecuentoItemResponseDTO.builder()
                    .idCategoria(hija.getId())
                    .nombre(hija.getNombre())
                    .numeroProductos(productos.size())
                    .existencia(totalesPorUnidad(productos, sumarStock(productos)))
                    .build());
        }
        return CategoriaRecuentoResponseDTO.builder()
                .idCategoria(categoria.getId())
                .nombre(categoria.getNombre())
                .subcategorias(filas)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoriaExistenciaResponseDTO existencia(UUID id) {
        CategoriaProductoEntity categoria = categoriaActiva(id);
        List<ProductoBasicoEntity> productos = productosEnRama(id);
        Map<UUID, BigDecimal> stock = sumarStock(productos);
        List<CategoriaExistenciaProductoResponseDTO> detalle = productos.stream()
                .map(producto -> CategoriaExistenciaProductoResponseDTO.builder()
                        .idProducto(producto.getId())
                        .nombre(producto.getNombre())
                        .cantidad(stock.getOrDefault(producto.getId(), BigDecimal.ZERO))
                        .unidad(unidad(producto))
                        .build())
                .toList();
        return CategoriaExistenciaResponseDTO.builder()
                .idCategoria(categoria.getId())
                .nombre(categoria.getNombre())
                .productos(detalle)
                .totales(totalesPorUnidad(productos, stock))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Set<UUID> expandirConSubcategorias(Collection<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return Set.of();
        }
        if (!SubcategoriaProductoBasico.habilitada()) {
            Set<UUID> directas = new HashSet<>();
            for (UUID id : ids) {
                if (id != null) {
                    directas.add(id);
                }
            }
            return directas;
        }
        Map<UUID, List<UUID>> hijasPorPadre = new HashMap<>();
        for (CategoriaProductoEntity categoria : repository.findAll()) {
            if (categoria.isDeleted() || categoria.getCategoriaPadre() == null) {
                continue;
            }
            hijasPorPadre
                    .computeIfAbsent(categoria.getCategoriaPadre().getId(), key -> new ArrayList<>())
                    .add(categoria.getId());
        }
        Set<UUID> resultado = new HashSet<>(ids);
        ArrayDeque<UUID> pendientes = new ArrayDeque<>(ids);
        while (!pendientes.isEmpty()) {
            UUID actual = pendientes.poll();
            for (UUID hija : hijasPorPadre.getOrDefault(actual, List.of())) {
                if (resultado.add(hija)) {
                    pendientes.add(hija);
                }
            }
        }
        return resultado;
    }

    public Specification<CategoriaProductoEntity> filterWithParameters(Map<String, String> params) {
        return new CateogriaSpecification().getSpecificationByFilters(params);
    }

    private CategoriaProductoEntity categoriaActiva(UUID id) {
        CategoriaProductoEntity categoria = repository.findById(id)
                .orElseThrow(() -> new IDNotFound("No se encontro la id proveeida: " + id));
        if (categoria.isDeleted()) {
            throw new IDNotFound("No se encontro la id proveeida: " + id);
        }
        return categoria;
    }

    private CategoriaProductoEntity resolverPadre(UUID categoriaPadreId) {
        if (categoriaPadreId == null) {
            return null;
        }
        CategoriaProductoEntity padre = repository.findById(categoriaPadreId)
                .orElseThrow(() -> new IDNotFound("No se encontró la categoría padre: " + categoriaPadreId));
        if (padre.isDeleted()) {
            throw new IDNotFound("No se encontró la categoría padre: " + categoriaPadreId);
        }
        return padre;
    }

    private void validarPadre(CategoriaProductoEntity categoria, CategoriaProductoEntity padre) {
        if (padre == null) {
            return;
        }
        if (categoria.getId() != null && categoria.getId().equals(padre.getId())) {
            throw new GenericExeption("La categoría no puede ser subcategoría de sí misma");
        }
        Set<UUID> visitados = new HashSet<>();
        CategoriaProductoEntity actual = padre;
        while (actual != null) {
            if (!visitados.add(actual.getId())) {
                throw new GenericExeption("La jerarquía de categorías tiene un ciclo");
            }
            if (categoria.getId() != null && categoria.getId().equals(actual.getId())) {
                throw new GenericExeption("La categoría no puede ser subcategoría de una de sus hijas");
            }
            actual = actual.getCategoriaPadre();
        }
    }

    private List<CategoriaProductoEntity> hijasDirectas(CategoriaProductoEntity categoria) {
        if (categoria.getSubcategorias() == null) {
            return List.of();
        }
        return categoria.getSubcategorias().stream()
                .filter(hija -> !hija.isDeleted())
                .sorted(Comparator.comparing(CategoriaProductoEntity::getNombre, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .toList();
    }

    private List<ProductoBasicoEntity> productosDirectos(UUID categoriaId) {
        return ordenarProductos(productoRepository.findByDeletedFalseAndEnabledTrueAndCategoriaProductoEntity_Id(categoriaId));
    }

    private List<ProductoBasicoEntity> productosEnRama(UUID categoriaId) {
        Set<UUID> ids = expandirConSubcategorias(Set.of(categoriaId));
        if (ids.isEmpty()) {
            return List.of();
        }
        return ordenarProductos(productoRepository.findByDeletedFalseAndEnabledTrueAndCategoriaProductoEntity_IdIn(ids));
    }

    private List<ProductoBasicoEntity> ordenarProductos(List<ProductoBasicoEntity> productos) {
        return productos.stream()
                .sorted(Comparator.comparing(ProductoBasicoEntity::getNombre, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .toList();
    }

    private CategoriaProductoItemResponseDTO aItem(ProductoBasicoEntity producto) {
        return CategoriaProductoItemResponseDTO.builder()
                .idProducto(producto.getId())
                .nombre(producto.getNombre())
                .sku(producto.getSku())
                .build();
    }

    private Map<UUID, BigDecimal> sumarStock(List<ProductoBasicoEntity> productos) {
        if (productos.isEmpty()) {
            return Map.of();
        }
        List<UUID> ids = productos.stream().map(ProductoBasicoEntity::getId).toList();
        Map<UUID, BigDecimal> stockPorProducto = new HashMap<>();
        for (Object[] fila : inventarioRepository.sumCantidadByProductoIds(ids)) {
            BigDecimal cantidad = (BigDecimal) fila[1];
            stockPorProducto.put((UUID) fila[0], cantidad != null ? cantidad : BigDecimal.ZERO);
        }
        return stockPorProducto;
    }

    private List<ExistenciaUnidadResponseDTO> totalesPorUnidad(List<ProductoBasicoEntity> productos, Map<UUID, BigDecimal> stock) {
        Map<String, BigDecimal> totales = new TreeMap<>();
        for (ProductoBasicoEntity producto : productos) {
            BigDecimal cantidad = stock.getOrDefault(producto.getId(), BigDecimal.ZERO);
            totales.merge(unidad(producto), cantidad, BigDecimal::add);
        }
        return totales.entrySet().stream()
                .map(entrada -> ExistenciaUnidadResponseDTO.builder()
                        .unidad(entrada.getKey())
                        .cantidad(entrada.getValue())
                        .build())
                .toList();
    }

    private String unidad(ProductoBasicoEntity producto) {
        if (producto.getUnidadMedidaDefault() == null || producto.getUnidadMedidaDefault().getNombre() == null
                || producto.getUnidadMedidaDefault().getNombre().isBlank()) {
            return SIN_UNIDAD;
        }
        return producto.getUnidadMedidaDefault().getNombre();
    }
}
