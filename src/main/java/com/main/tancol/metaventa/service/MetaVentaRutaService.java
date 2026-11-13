package com.main.tancol.metaventa.service;

import com.main.tancol.common.exeptions.IDNotFound;
import com.main.tancol.logistica.entities.rutas.RutasEntity;
import com.main.tancol.logistica.repositories.RutasRepository;
import com.main.tancol.metaventa.dto.request.MetaVentaRutaRequestDTO;
import com.main.tancol.metaventa.dto.request.MetaVentaUpdateRequestDTO;
import com.main.tancol.metaventa.dto.response.MetaVentaRutaResponseDTO;
import com.main.tancol.metaventa.entities.MetaVentaRutaEntity;
import com.main.tancol.metaventa.enums.PeriodoMetaVenta;
import com.main.tancol.metaventa.repositories.MetaVentaRutaRepository;
import com.main.tancol.metaventa.service.interfaces.IMetaVentaRutaService;
import com.main.tancol.metaventa.util.MetaVentaRutaSpecification;
import com.main.tancol.metaventa.util.MetaVentaRutaValueMapper;
import com.main.tancol.producto.entities.comerciales.ProductoComercialEntity;
import com.main.tancol.producto.repositories.ProductoComercialRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class MetaVentaRutaService implements IMetaVentaRutaService {

    private final MetaVentaRutaRepository repository;
    private final RutasRepository rutasRepository;
    private final ProductoComercialRepository productoRepository;

    public MetaVentaRutaService(MetaVentaRutaRepository repository,
                                RutasRepository rutasRepository,
                                ProductoComercialRepository productoRepository) {
        this.repository = repository;
        this.rutasRepository = rutasRepository;
        this.productoRepository = productoRepository;
    }

    @Override
    @Transactional
    public MetaVentaRutaResponseDTO create(MetaVentaRutaRequestDTO dto) {
        validarCantidad(dto.getCantidadMinima());

        RutasEntity ruta = rutasRepository.findById(dto.getIdRuta())
                .orElseThrow(() -> new IDNotFound("No se encontró la ruta"));
        if (ruta.isDeleted()) {
            throw new IDNotFound("La ruta está eliminada");
        }

        ProductoComercialEntity producto = productoRepository.findById(dto.getIdProducto())
                .orElseThrow(() -> new IDNotFound("No se encontró el producto"));
        if (producto.isDeleted()) {
            throw new IDNotFound("El producto está eliminado");
        }

        if (repository.existsByRutaIdAndProductoIdAndActivaTrueAndDeletedFalse(ruta.getId(), producto.getId())) {
            throw new IDNotFound("Ya existe una meta activa para esa ruta y producto");
        }

        MetaVentaRutaEntity meta = MetaVentaRutaEntity.builder()
                .ruta(ruta)
                .producto(producto)
                .cantidadMinima(dto.getCantidadMinima())
                .periodo(PeriodoMetaVenta.SEMANAL)
                .activa(true)
                .fechaInicio(LocalDateTime.now())
                .build();

        return MetaVentaRutaValueMapper.entityToResponse(repository.save(meta));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MetaVentaRutaResponseDTO> getAll(Map<String, String> params) {
        return repository.findAll(filterWithParameters(params)).stream()
                .map(MetaVentaRutaValueMapper::entityToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MetaVentaRutaResponseDTO getById(UUID id) {
        return MetaVentaRutaValueMapper.entityToResponse(buscar(id));
    }

    @Override
    @Transactional
    public MetaVentaRutaResponseDTO update(UUID id, MetaVentaUpdateRequestDTO dto) {
        validarCantidad(dto.getCantidadMinima());
        MetaVentaRutaEntity meta = buscar(id);
        if (!meta.isActiva()) {
            throw new IDNotFound("La meta ya fue cerrada");
        }
        meta.setCantidadMinima(dto.getCantidadMinima());
        return MetaVentaRutaValueMapper.entityToResponse(repository.save(meta));
    }

    @Override
    @Transactional
    public MetaVentaRutaResponseDTO cerrar(UUID id) {
        MetaVentaRutaEntity meta = buscar(id);
        if (!meta.isActiva()) {
            throw new IDNotFound("La meta ya fue cerrada");
        }
        meta.setActiva(false);
        meta.setFechaCierre(LocalDateTime.now());
        return MetaVentaRutaValueMapper.entityToResponse(repository.save(meta));
    }

    private MetaVentaRutaEntity buscar(UUID id) {
        MetaVentaRutaEntity meta = repository.findById(id)
                .orElseThrow(() -> new IDNotFound("No se encontró la meta"));
        if (meta.isDeleted()) {
            throw new IDNotFound("No se encontró la meta");
        }
        return meta;
    }

    private void validarCantidad(BigDecimal cantidad) {
        if (cantidad == null || cantidad.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("La cantidad mínima es obligatoria y no puede ser negativa");
        }
    }

    private Specification<MetaVentaRutaEntity> filterWithParameters(Map<String, String> params) {
        return new MetaVentaRutaSpecification().getSpecificationByFilters(params);
    }
}
