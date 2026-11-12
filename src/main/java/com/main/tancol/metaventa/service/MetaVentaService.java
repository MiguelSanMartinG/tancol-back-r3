package com.main.tancol.metaventa.service;

import com.main.tancol.common.exeptions.IDNotFound;
import com.main.tancol.empleados.entities.EmpleadosEntity;
import com.main.tancol.empleados.repositories.EmpleadosRepository;
import com.main.tancol.metaventa.dto.request.MetaVentaRequestDTO;
import com.main.tancol.metaventa.dto.request.MetaVentaUpdateRequestDTO;
import com.main.tancol.metaventa.dto.response.MetaVentaResponseDTO;
import com.main.tancol.metaventa.entities.MetaVentaEmpleadoEntity;
import com.main.tancol.metaventa.enums.PeriodoMetaVenta;
import com.main.tancol.metaventa.repositories.MetaVentaEmpleadoRepository;
import com.main.tancol.metaventa.service.interfaces.IMetaVentaService;
import com.main.tancol.metaventa.util.MetaVentaSpecification;
import com.main.tancol.metaventa.util.MetaVentaValueMapper;
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
public class MetaVentaService implements IMetaVentaService {

    private final MetaVentaEmpleadoRepository repository;
    private final EmpleadosRepository empleadosRepository;
    private final ProductoComercialRepository productoRepository;

    public MetaVentaService(MetaVentaEmpleadoRepository repository,
                            EmpleadosRepository empleadosRepository,
                            ProductoComercialRepository productoRepository) {
        this.repository = repository;
        this.empleadosRepository = empleadosRepository;
        this.productoRepository = productoRepository;
    }

    @Override
    @Transactional
    public MetaVentaResponseDTO create(MetaVentaRequestDTO dto) {
        validarCantidad(dto.getCantidadMinima());

        EmpleadosEntity empleado = empleadosRepository.findById(dto.getIdEmpleado())
                .orElseThrow(() -> new IDNotFound("No se encontró el empleado"));
        if (empleado.isDeleted()) {
            throw new IDNotFound("El empleado está eliminado");
        }

        ProductoComercialEntity producto = productoRepository.findById(dto.getIdProducto())
                .orElseThrow(() -> new IDNotFound("No se encontró el producto"));
        if (producto.isDeleted()) {
            throw new IDNotFound("El producto está eliminado");
        }

        if (repository.existsByEmpleadoIdAndProductoIdAndActivaTrueAndDeletedFalse(empleado.getId(), producto.getId())) {
            throw new IDNotFound("Ya existe una meta activa para ese empleado y producto");
        }

        MetaVentaEmpleadoEntity meta = MetaVentaEmpleadoEntity.builder()
                .empleado(empleado)
                .producto(producto)
                .cantidadMinima(dto.getCantidadMinima())
                .periodo(PeriodoMetaVenta.SEMANAL)
                .activa(true)
                .fechaInicio(LocalDateTime.now())
                .build();

        return MetaVentaValueMapper.entityToResponse(repository.save(meta));
    }

    @Override
    public List<MetaVentaResponseDTO> getAll(Map<String, String> params) {
        return repository.findAll(filterWithParameters(params)).stream()
                .map(MetaVentaValueMapper::entityToResponse)
                .toList();
    }

    @Override
    public MetaVentaResponseDTO getById(UUID id) {
        return MetaVentaValueMapper.entityToResponse(buscar(id));
    }

    @Override
    @Transactional
    public MetaVentaResponseDTO update(UUID id, MetaVentaUpdateRequestDTO dto) {
        validarCantidad(dto.getCantidadMinima());
        MetaVentaEmpleadoEntity meta = buscar(id);
        if (!meta.isActiva()) {
            throw new IDNotFound("La meta ya fue cerrada");
        }
        meta.setCantidadMinima(dto.getCantidadMinima());
        return MetaVentaValueMapper.entityToResponse(repository.save(meta));
    }

    @Override
    @Transactional
    public MetaVentaResponseDTO cerrar(UUID id) {
        MetaVentaEmpleadoEntity meta = buscar(id);
        if (!meta.isActiva()) {
            throw new IDNotFound("La meta ya fue cerrada");
        }
        meta.setActiva(false);
        meta.setFechaCierre(LocalDateTime.now());
        return MetaVentaValueMapper.entityToResponse(repository.save(meta));
    }

    private MetaVentaEmpleadoEntity buscar(UUID id) {
        MetaVentaEmpleadoEntity meta = repository.findById(id)
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

    private Specification<MetaVentaEmpleadoEntity> filterWithParameters(Map<String, String> params) {
        return new MetaVentaSpecification().getSpecificationByFilters(params);
    }
}
