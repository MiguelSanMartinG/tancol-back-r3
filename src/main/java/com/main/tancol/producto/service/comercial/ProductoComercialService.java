package com.main.tancol.producto.service.comercial;

import com.main.tancol.almacen.entities.UnidadesMedidaEntity;
import com.main.tancol.almacen.repositories.UnidadesMedidaRepository;
import com.main.tancol.common.dto.CatalogPage;
import com.main.tancol.common.dto.PageResponse;
import com.main.tancol.common.exeptions.IDNotFound;
import com.main.tancol.producto.dto.request.comercial.ProductoComercialRequestDTO;
import com.main.tancol.producto.dto.response.comercial.ProductoComercialResponseDTO;
import com.main.tancol.producto.entities.comerciales.CategoriaProductoComercialEntity;
import com.main.tancol.producto.entities.comerciales.ProductoComercialEntity;
import com.main.tancol.producto.repositories.CategoriaProductoComercialRepository;
import com.main.tancol.producto.repositories.ProductoComercialRepository;
import com.main.tancol.producto.service.interfaces.IProductoComercialService;
import com.main.tancol.producto.util.comercial.ProductoComercialPageSpecification;
import com.main.tancol.producto.util.comercial.ProductoComercialSpecification;
import com.main.tancol.producto.util.comercial.ProductoComercialValueMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductoComercialService implements IProductoComercialService {

    private final ProductoComercialRepository repository;
    private final CategoriaProductoComercialRepository categoriaService;
    private final UnidadesMedidaRepository unidadesMedidaRepository;


    @Override
    public ProductoComercialResponseDTO create(ProductoComercialRequestDTO dto) {
        boolean existe = repository.existsByNombre(dto.getNombre());
        if (existe) {
            throw new IDNotFound("El nombre ya existe");
        }
        CategoriaProductoComercialEntity categoria = categoriaService.findById(dto.getIdCategoriaComercial()).orElseThrow(()-> new RuntimeException("Categoria comercial no encontrada"));
        UnidadesMedidaEntity unidadesMedida = null;
        if(dto.getUnidadMedidaDefault() != null){
            unidadesMedida = unidadesMedidaRepository.findById(dto.getUnidadMedidaDefault()).orElseThrow(()-> new IDNotFound("No existe unidad medida"));
        }

        ProductoComercialEntity entity = ProductoComercialValueMapper.ProductoComercialRequestToEntity(dto,categoria,unidadesMedida);
        return ProductoComercialValueMapper.ProductoComercialEntityToResponse(this.repository.save(entity));
    }

    @Override
    public ProductoComercialResponseDTO getById(UUID id) {
        ProductoComercialEntity producto = this.repository.findById(id).orElseThrow(() -> new IDNotFound("Producto no encontrado"));
        return ProductoComercialValueMapper.ProductoComercialEntityToResponse(producto);
    }

    @Override
    public ProductoComercialResponseDTO update(UUID id, ProductoComercialRequestDTO dto) {
        CategoriaProductoComercialEntity categoria = categoriaService.findById(dto.getIdCategoriaComercial()).orElseThrow(()-> new IDNotFound("Categoria comercial no encontrada"));
        ProductoComercialEntity producto = this.repository.findById(id).orElseThrow(() -> new IDNotFound("Producto no encontrado"));
        UnidadesMedidaEntity unidadesMedida = null;
        if(dto.getUnidadMedidaDefault() != null){
            unidadesMedida = unidadesMedidaRepository.findById(dto.getUnidadMedidaDefault()).orElseThrow(()-> new IDNotFound("No existe unidad medida"));
        }

//        boolean existe = repository.existsByNombre(dto.getNombre());
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setAlias(dto.getAlias());
        producto.setCategoriaProductoComercialEntity(categoria);
        producto.setIdImagen(dto.getIdImagen());
        producto.setSku(dto.getSku());
        producto.setEnabled(dto.isEnabled());
        producto.setUnidadMedidaDefault(unidadesMedida);
        producto.setPresentacion(dto.getPresentacion());
        return ProductoComercialValueMapper.ProductoComercialEntityToResponse(repository.save(producto));

    }

    @Override
    public ProductoComercialResponseDTO delete(UUID id) {
        ProductoComercialEntity producto = this.repository.findById(id).orElseThrow(() -> new IDNotFound("Producto no encontrado"));
        producto.setDeleted(true);
        return ProductoComercialValueMapper.ProductoComercialEntityToResponse(repository.save(producto));
    }

    @Override
    public List<ProductoComercialResponseDTO> getAll(Map<String, String> params) {
        List<ProductoComercialEntity> list = repository.findAll(filterWithParameters(params));
        return list.stream().map(ProductoComercialValueMapper::ProductoComercialEntityToResponse).toList();
    }

    private static final Set<String> PAGE_FILTERS = Set.of(
            "sku", "nombre", "alias", "presentacion", "precio", "enabled", "createdAt", "updatedAt"
    );
    private static final Map<String, String> PAGE_SORTS = Map.of(
            "sku", "sku",
            "nombre", "nombre",
            "alias", "alias",
            "presentacion", "presentacion",
            "precio", "precio",
            "enabled", "enabled",
            "createdAt", "createdAt",
            "updatedAt", "updatedAt"
    );

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductoComercialResponseDTO> getPage(Map<String, String> params) {
        CatalogPage query = CatalogPage.parse(params, PAGE_FILTERS, PAGE_SORTS.keySet());
        var result = repository.findAll(
                new ProductoComercialPageSpecification(query.search(), query.filters()),
                query.pageable(query.sort(PAGE_SORTS, Sort.by(Sort.Direction.DESC, "createdAt"), "id"))
        );
        var items = result.getContent().stream().map(ProductoComercialValueMapper::ProductoComercialEntityToResponse).toList();
        return new PageResponse<>(items, result.getTotalElements(), query.page(), query.pageSize());
    }

    public Specification<ProductoComercialEntity> filterWithParameters(Map<String, String> params) {
        return new ProductoComercialSpecification().getSpecificationByFilters(params);
    }
}
