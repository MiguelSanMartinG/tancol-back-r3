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
import com.main.tancol.tablascomisiones.service.interfaces.ITablasComisionesService;
import com.main.tancol.tablascomisiones.util.TablasComisionesSpecification;
import com.main.tancol.tablascomisiones.util.TablasComisionesValueMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class TablasComisionesService implements ITablasComisionesService {

    private final TablasComisionesRepository repository;
    private final ProductoComercialRepository productoRepository;

    public TablasComisionesService(TablasComisionesRepository repository,
                                   ProductoComercialRepository productoRepository) {
        this.repository = repository;
        this.productoRepository = productoRepository;
    }

    @Override
    @Transactional
    public TablasComisionesResponseDTO create(TablasComisionesRequestDTO dto) {
        validarRango(dto);
        BigDecimal comision = normalizarComision(dto.getComision());
        ProductoComercialEntity producto = buscarProductoActivo(dto.getIdProducto());
        if (repository.existsByProducto_IdAndDeletedFalse(producto.getId())) {
            throw new PropiedaDuplicada("Ese producto ya tiene una tabla de comisiones");
        }

        TablasComisionesEntity entity = TablasComisionesEntity.builder()
                .ventaMinima(dto.getVentaMinima())
                .cantidadDesde(dto.getCantidadDesde())
                .cantidadHasta(dto.getCantidadHasta())
                .comision(comision)
                .producto(producto)
                .build();

        return guardar(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TablasComisionesResponseDTO> getAll(Map<String, String> params) {
        Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt");
        return repository.findAll(filterWithParameters(params), sort).stream()
                .map(TablasComisionesValueMapper::entityToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TablasComisionesResponseDTO getById(UUID id) {
        return TablasComisionesValueMapper.entityToResponse(buscar(id));
    }

    @Override
    @Transactional
    public TablasComisionesResponseDTO update(UUID id, TablasComisionesRequestDTO dto) {
        validarRango(dto);
        BigDecimal comision = normalizarComision(dto.getComision());
        TablasComisionesEntity entity = buscar(id);
        ProductoComercialEntity producto = buscarProductoActivo(dto.getIdProducto());
        if (repository.existsByProducto_IdAndDeletedFalseAndIdNot(producto.getId(), id)) {
            throw new PropiedaDuplicada("Ese producto ya tiene una tabla de comisiones");
        }

        entity.setVentaMinima(dto.getVentaMinima());
        entity.setCantidadDesde(dto.getCantidadDesde());
        entity.setCantidadHasta(dto.getCantidadHasta());
        entity.setComision(comision);
        entity.setProducto(producto);
        return guardar(entity);
    }

    @Override
    @Transactional
    public TablasComisionesResponseDTO delete(UUID id) {
        TablasComisionesEntity entity = buscar(id);
        entity.setDeleted(true);
        return TablasComisionesValueMapper.entityToResponse(repository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoComisionDisponibleResponseDTO> productosDisponibles(UUID tablaId) {
        return repository.findProductosDisponibles(tablaId).stream()
                .map(TablasComisionesValueMapper::productoDisponible)
                .toList();
    }

    private TablasComisionesResponseDTO guardar(TablasComisionesEntity entity) {
        try {
            return TablasComisionesValueMapper.entityToResponse(repository.save(entity));
        } catch (DataIntegrityViolationException ex) {
            throw new PropiedaDuplicada("Ese producto ya tiene una tabla de comisiones");
        }
    }

    private TablasComisionesEntity buscar(UUID id) {
        TablasComisionesEntity entity = repository.findById(id)
                .orElseThrow(() -> new IDNotFound("No se encontró la tabla de comisiones"));
        if (entity.isDeleted()) {
            throw new IDNotFound("No se encontró la tabla de comisiones");
        }
        return entity;
    }

    private ProductoComercialEntity buscarProductoActivo(UUID idProducto) {
        ProductoComercialEntity producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new IDNotFound("No se encontró el producto"));
        if (producto.isDeleted() || !producto.isEnabled()) {
            throw new IDNotFound("El producto no está disponible");
        }
        return producto;
    }

    private void validarRango(TablasComisionesRequestDTO dto) {
        if (dto.getVentaMinima() == null || dto.getVentaMinima() < 1) {
            throw new PropiedaDuplicada("La venta mínima debe ser un entero mayor o igual a 1");
        }
        if (dto.getCantidadDesde() == null || dto.getCantidadDesde() < 1) {
            throw new PropiedaDuplicada("De debe ser un entero mayor o igual a 1");
        }
        if (dto.getCantidadHasta() == null || dto.getCantidadHasta() < dto.getCantidadDesde()) {
            throw new PropiedaDuplicada("A debe ser igual o mayor que De");
        }
    }

    private BigDecimal normalizarComision(BigDecimal comision) {
        if (comision == null) {
            throw new PropiedaDuplicada("La comisión es obligatoria");
        }
        if (comision.scale() > 2) {
            throw new PropiedaDuplicada("La comisión admite hasta dos decimales");
        }
        if (comision.compareTo(BigDecimal.ZERO) < 0) {
            throw new PropiedaDuplicada("La comisión no puede ser negativa");
        }
        return comision.setScale(2, RoundingMode.UNNECESSARY);
    }

    private Specification<TablasComisionesEntity> filterWithParameters(Map<String, String> params) {
        return new TablasComisionesSpecification().getSpecificationByFilters(params);
    }
}
