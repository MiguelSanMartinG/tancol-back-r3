package com.main.tancol.tabuladorrotos.service;

import com.main.tancol.almacen.entities.UnidadesMedidaEntity;
import com.main.tancol.almacen.repositories.UnidadesMedidaRepository;
import com.main.tancol.common.exeptions.IDNotFound;
import com.main.tancol.common.exceptions.exeption.GenericExeption;
import com.main.tancol.common.exceptions.exeption.PropiedaDuplicada;
import com.main.tancol.empleados.entities.EmpleadosEntity;
import com.main.tancol.empleados.repositories.EmpleadosRepository;
import com.main.tancol.producto.entities.comerciales.ProductoComercialEntity;
import com.main.tancol.producto.repositories.ProductoComercialRepository;
import com.main.tancol.tabuladorrotos.dto.request.TabuladorBotellonRotoRequestDTO;
import com.main.tancol.tabuladorrotos.dto.response.TabuladorBotellonRotoResponseDTO;
import com.main.tancol.tabuladorrotos.entities.TabuladorBotellonRotoEntity;
import com.main.tancol.tabuladorrotos.enums.PeriodoTabuladorRoto;
import com.main.tancol.tabuladorrotos.repositories.TabuladorBotellonRotoRepository;
import com.main.tancol.tabuladorrotos.service.interfaces.ITabuladorBotellonRotoService;
import com.main.tancol.tabuladorrotos.util.TabuladorBotellonRotoSpecification;
import com.main.tancol.tabuladorrotos.util.TabuladorBotellonRotoValueMapper;
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
public class TabuladorBotellonRotoService implements ITabuladorBotellonRotoService {

    private final TabuladorBotellonRotoRepository repository;
    private final EmpleadosRepository empleadosRepository;
    private final ProductoComercialRepository productoRepository;
    private final UnidadesMedidaRepository unidadesMedidaRepository;

    public TabuladorBotellonRotoService(TabuladorBotellonRotoRepository repository,
                                        EmpleadosRepository empleadosRepository,
                                        ProductoComercialRepository productoRepository,
                                        UnidadesMedidaRepository unidadesMedidaRepository) {
        this.repository = repository;
        this.empleadosRepository = empleadosRepository;
        this.productoRepository = productoRepository;
        this.unidadesMedidaRepository = unidadesMedidaRepository;
    }

    @Override
    @Transactional
    public TabuladorBotellonRotoResponseDTO create(TabuladorBotellonRotoRequestDTO dto) {
        validar(dto);
        EmpleadosEntity empleado = buscarEmpleado(dto.getIdEmpleado());
        ProductoComercialEntity producto = buscarProducto(dto.getIdProducto());
        if (repository.existsByEmpleado_IdAndProducto_IdAndDeletedFalse(empleado.getId(), producto.getId())) {
            throw new PropiedaDuplicada("Ese empleado ya tiene un tabulador para ese producto");
        }

        TabuladorBotellonRotoEntity entity = TabuladorBotellonRotoEntity.builder()
                .empleado(empleado)
                .periodo(PeriodoTabuladorRoto.SEMANAL)
                .tolerancia(normalizarTolerancia(dto.getTolerancia()))
                .producto(producto)
                .unidadMedida(buscarUnidad(dto.getIdUnidadMedida()))
                .build();
        return guardar(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TabuladorBotellonRotoResponseDTO> getAll(Map<String, String> params) {
        Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt");
        return repository.findAll(filterWithParameters(params), sort).stream()
                .map(TabuladorBotellonRotoValueMapper::entityToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TabuladorBotellonRotoResponseDTO getById(UUID id) {
        return TabuladorBotellonRotoValueMapper.entityToResponse(buscar(id));
    }

    @Override
    @Transactional
    public TabuladorBotellonRotoResponseDTO update(UUID id, TabuladorBotellonRotoRequestDTO dto) {
        validar(dto);
        TabuladorBotellonRotoEntity entity = buscar(id);
        EmpleadosEntity empleado = buscarEmpleado(dto.getIdEmpleado());
        ProductoComercialEntity producto = buscarProducto(dto.getIdProducto());
        if (repository.existsByEmpleado_IdAndProducto_IdAndDeletedFalseAndIdNot(empleado.getId(), producto.getId(), id)) {
            throw new PropiedaDuplicada("Ese empleado ya tiene un tabulador para ese producto");
        }
        entity.setEmpleado(empleado);
        entity.setPeriodo(PeriodoTabuladorRoto.SEMANAL);
        entity.setTolerancia(normalizarTolerancia(dto.getTolerancia()));
        entity.setProducto(producto);
        entity.setUnidadMedida(buscarUnidad(dto.getIdUnidadMedida()));
        return guardar(entity);
    }

    @Override
    @Transactional
    public TabuladorBotellonRotoResponseDTO delete(UUID id) {
        TabuladorBotellonRotoEntity entity = buscar(id);
        entity.setDeleted(true);
        return TabuladorBotellonRotoValueMapper.entityToResponse(repository.save(entity));
    }

    private TabuladorBotellonRotoResponseDTO guardar(TabuladorBotellonRotoEntity entity) {
        try {
            return TabuladorBotellonRotoValueMapper.entityToResponse(repository.save(entity));
        } catch (DataIntegrityViolationException ex) {
            throw new PropiedaDuplicada("Ese empleado ya tiene un tabulador para ese producto");
        }
    }

    private TabuladorBotellonRotoEntity buscar(UUID id) {
        TabuladorBotellonRotoEntity entity = repository.findById(id)
                .orElseThrow(() -> new IDNotFound("No se encontró el tabulador de botellones rotos"));
        if (entity.isDeleted()) {
            throw new IDNotFound("No se encontró el tabulador de botellones rotos");
        }
        return entity;
    }

    private void validar(TabuladorBotellonRotoRequestDTO dto) {
        if (dto.getPeriodo() != PeriodoTabuladorRoto.SEMANAL) {
            throw new GenericExeption("El periodo del tabulador es semanal");
        }
    }

    private BigDecimal normalizarTolerancia(BigDecimal tolerancia) {
        if (tolerancia == null || tolerancia.compareTo(BigDecimal.ZERO) <= 0) {
            throw new GenericExeption("La tolerancia debe ser mayor a 0");
        }
        if (tolerancia.scale() > 2) {
            throw new GenericExeption("La tolerancia admite hasta dos decimales");
        }
        return tolerancia.setScale(2, RoundingMode.UNNECESSARY);
    }

    private EmpleadosEntity buscarEmpleado(UUID id) {
        EmpleadosEntity empleado = empleadosRepository.findById(id)
                .orElseThrow(() -> new IDNotFound("No se encontró el empleado"));
        if (empleado.isDeleted() || !empleado.isEnabled()) {
            throw new IDNotFound("El empleado no está disponible");
        }
        return empleado;
    }

    private ProductoComercialEntity buscarProducto(UUID id) {
        ProductoComercialEntity producto = productoRepository.findById(id)
                .orElseThrow(() -> new IDNotFound("No se encontró el producto"));
        if (producto.isDeleted() || !producto.isEnabled()) {
            throw new IDNotFound("El producto no está disponible");
        }
        return producto;
    }

    private UnidadesMedidaEntity buscarUnidad(UUID id) {
        UnidadesMedidaEntity unidad = unidadesMedidaRepository.findById(id)
                .orElseThrow(() -> new IDNotFound("No se encontró la unidad de medida"));
        if (unidad.isDeleted() || !unidad.isEnabled()) {
            throw new IDNotFound("La unidad de medida no está disponible");
        }
        return unidad;
    }

    private Specification<TabuladorBotellonRotoEntity> filterWithParameters(Map<String, String> params) {
        return new TabuladorBotellonRotoSpecification().getSpecificationByFilters(params);
    }
}
