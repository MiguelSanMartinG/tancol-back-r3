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
import com.main.tancol.tablasamonestaciones.service.interfaces.ITablasAmonestacionesService;
import com.main.tancol.tablasamonestaciones.util.TablasAmonestacionesSpecification;
import com.main.tancol.tablasamonestaciones.util.TablasAmonestacionesValueMapper;
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
public class TablasAmonestacionesService implements ITablasAmonestacionesService {

    private final TablasAmonestacionesRepository repository;
    private final ProductoComercialRepository productoRepository;

    public TablasAmonestacionesService(TablasAmonestacionesRepository repository,
                                       ProductoComercialRepository productoRepository) {
        this.repository = repository;
        this.productoRepository = productoRepository;
    }

    @Override
    @Transactional
    public TablasAmonestacionesResponseDTO create(TablasAmonestacionesRequestDTO dto) {
        validarRango(dto);
        BigDecimal descuento = normalizarDescuento(dto.getDescuento());
        ProductoComercialEntity producto = buscarProductoActivo(dto.getIdProducto());
        if (repository.existsByProducto_IdAndDeletedFalse(producto.getId())) {
            throw new PropiedaDuplicada("Ese producto ya tiene una tabla de amonestación");
        }

        TablasAmonestacionesEntity entity = TablasAmonestacionesEntity.builder()
                .cantidadDesde(dto.getCantidadDesde())
                .cantidadHasta(dto.getCantidadHasta())
                .descuento(descuento)
                .producto(producto)
                .build();
        return guardar(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TablasAmonestacionesResponseDTO> getAll(Map<String, String> params) {
        Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt");
        return repository.findAll(filterWithParameters(params), sort).stream()
                .map(TablasAmonestacionesValueMapper::entityToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TablasAmonestacionesResponseDTO getById(UUID id) {
        return TablasAmonestacionesValueMapper.entityToResponse(buscar(id));
    }

    @Override
    @Transactional
    public TablasAmonestacionesResponseDTO update(UUID id, TablasAmonestacionesRequestDTO dto) {
        validarRango(dto);
        BigDecimal descuento = normalizarDescuento(dto.getDescuento());
        TablasAmonestacionesEntity entity = buscar(id);
        ProductoComercialEntity producto = buscarProductoActivo(dto.getIdProducto());
        if (repository.existsByProducto_IdAndDeletedFalseAndIdNot(producto.getId(), id)) {
            throw new PropiedaDuplicada("Ese producto ya tiene una tabla de amonestación");
        }
        entity.setCantidadDesde(dto.getCantidadDesde());
        entity.setCantidadHasta(dto.getCantidadHasta());
        entity.setDescuento(descuento);
        entity.setProducto(producto);
        return guardar(entity);
    }

    @Override
    @Transactional
    public TablasAmonestacionesResponseDTO delete(UUID id) {
        TablasAmonestacionesEntity entity = buscar(id);
        entity.setDeleted(true);
        return TablasAmonestacionesValueMapper.entityToResponse(repository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoAmonestacionDisponibleResponseDTO> productosDisponibles(UUID tablaId) {
        return repository.findProductosDisponibles(tablaId).stream()
                .map(TablasAmonestacionesValueMapper::productoDisponible)
                .toList();
    }

    private TablasAmonestacionesResponseDTO guardar(TablasAmonestacionesEntity entity) {
        try {
            return TablasAmonestacionesValueMapper.entityToResponse(repository.save(entity));
        } catch (DataIntegrityViolationException ex) {
            throw new PropiedaDuplicada("Ese producto ya tiene una tabla de amonestación");
        }
    }

    private TablasAmonestacionesEntity buscar(UUID id) {
        TablasAmonestacionesEntity entity = repository.findById(id)
                .orElseThrow(() -> new IDNotFound("No se encontró la tabla de amonestación"));
        if (entity.isDeleted()) {
            throw new IDNotFound("No se encontró la tabla de amonestación");
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

    private void validarRango(TablasAmonestacionesRequestDTO dto) {
        if (dto.getCantidadDesde() == null || dto.getCantidadDesde() < 1) {
            throw new PropiedaDuplicada("De debe ser un entero mayor o igual a 1");
        }
        if (dto.getCantidadHasta() == null || dto.getCantidadHasta() < dto.getCantidadDesde()) {
            throw new PropiedaDuplicada("A debe ser igual o mayor que De");
        }
    }

    private BigDecimal normalizarDescuento(BigDecimal descuento) {
        if (descuento == null) {
            throw new PropiedaDuplicada("El descuento es obligatorio");
        }
        if (descuento.scale() > 2) {
            throw new PropiedaDuplicada("El descuento admite hasta dos decimales");
        }
        if (descuento.compareTo(BigDecimal.ZERO) > 0) {
            throw new PropiedaDuplicada("El descuento no puede ser positivo");
        }
        return descuento.setScale(2, RoundingMode.UNNECESSARY);
    }

    private Specification<TablasAmonestacionesEntity> filterWithParameters(Map<String, String> params) {
        return new TablasAmonestacionesSpecification().getSpecificationByFilters(params);
    }
}
