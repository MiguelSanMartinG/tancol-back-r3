package com.main.tancol.almacen.service;

import com.main.tancol.almacen.dto.request.WhitelistAsignacionDepartamentoRequestDTO;
import com.main.tancol.almacen.dto.request.WhitelistAsignacionMasivaRequestDTO;
import com.main.tancol.almacen.dto.request.WhitelistConfiguracionRequestDTO;
import com.main.tancol.almacen.dto.request.WhitelistProductoRequestDTO;
import com.main.tancol.almacen.dto.request.WhitelistProductoUpdateRequestDTO;
import com.main.tancol.almacen.dto.response.ProductosPermitidosResponseDTO;
import com.main.tancol.almacen.dto.response.WhitelistAsignacionResponseDTO;
import com.main.tancol.almacen.dto.response.WhitelistConfiguracionResponseDTO;
import com.main.tancol.almacen.dto.response.WhitelistProductoResponseDTO;
import com.main.tancol.almacen.entities.AlmacenConfiguracionEntity;
import com.main.tancol.almacen.entities.WhitelistProductoEmpleadoEntity;
import com.main.tancol.almacen.repositories.AlmacenConfiguracionRepository;
import com.main.tancol.almacen.repositories.WhitelistProductoEmpleadoRepository;
import com.main.tancol.almacen.service.interfaces.IWhitelistProductoService;
import com.main.tancol.almacen.util.WhitelistProductoSpecification;
import com.main.tancol.almacen.util.WhitelistProductoValueMapper;
import com.main.tancol.common.exeptions.IDNotFound;
import com.main.tancol.empleados.entities.DepartamentoEntity;
import com.main.tancol.empleados.entities.EmpleadosEntity;
import com.main.tancol.empleados.helpers.EmpleadoContext;
import com.main.tancol.empleados.repositories.DepartamentoRepository;
import com.main.tancol.empleados.repositories.EmpleadosRepository;
import com.main.tancol.empleados.util.DepartamentoMapper;
import com.main.tancol.producto.entities.insumos.ProductoBasicoEntity;
import com.main.tancol.producto.repositories.ProductoRepository;
import com.main.tancol.producto.util.insumos.ProductoValueMapper;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class WhitelistProductoService implements IWhitelistProductoService {

    private final WhitelistProductoEmpleadoRepository repository;
    private final AlmacenConfiguracionRepository configuracionRepository;
    private final EmpleadosRepository empleadosRepository;
    private final DepartamentoRepository departamentoRepository;
    private final ProductoRepository productoRepository;

    public WhitelistProductoService(WhitelistProductoEmpleadoRepository repository,
                                    AlmacenConfiguracionRepository configuracionRepository,
                                    EmpleadosRepository empleadosRepository,
                                    DepartamentoRepository departamentoRepository,
                                    ProductoRepository productoRepository) {
        this.repository = repository;
        this.configuracionRepository = configuracionRepository;
        this.empleadosRepository = empleadosRepository;
        this.departamentoRepository = departamentoRepository;
        this.productoRepository = productoRepository;
    }

    @Override
    @Transactional
    public WhitelistProductoResponseDTO create(WhitelistProductoRequestDTO dto) {
        EmpleadosEntity empleado = empleadoActivo(dto.getIdEmpleado());
        ProductoBasicoEntity producto = productoActivo(dto.getIdProducto());
        if (repository.findFirstByEmpleado_IdAndProducto_IdAndDeletedFalse(empleado.getId(), producto.getId()).isPresent()) {
            throw new IDNotFound("El producto ya está asignado a este empleado");
        }
        WhitelistProductoEmpleadoEntity entity = WhitelistProductoEmpleadoEntity.builder()
                .empleado(empleado)
                .producto(producto)
                .build();
        return WhitelistProductoValueMapper.toResponse(repository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public List<WhitelistProductoResponseDTO> getAll(Map<String, String> params) {
        Specification<WhitelistProductoEmpleadoEntity> specification = new WhitelistProductoSpecification().getSpecificationByFilters(params);
        List<WhitelistProductoEmpleadoEntity> list = repository.findAll(specification);
        return list.stream().map(WhitelistProductoValueMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public WhitelistProductoResponseDTO getById(UUID id) {
        return WhitelistProductoValueMapper.toResponse(asignacionActiva(id));
    }

    @Override
    @Transactional
    public WhitelistProductoResponseDTO update(UUID id, WhitelistProductoUpdateRequestDTO dto) {
        WhitelistProductoEmpleadoEntity entity = asignacionActiva(id);
        EmpleadosEntity empleado = empleadoActivo(dto.getIdEmpleado());
        ProductoBasicoEntity producto = productoActivo(dto.getIdProducto());
        Optional<WhitelistProductoEmpleadoEntity> duplicado = repository.findFirstByEmpleado_IdAndProducto_IdAndDeletedFalse(empleado.getId(), producto.getId());
        if (duplicado.isPresent() && !duplicado.get().getId().equals(entity.getId())) {
            throw new IDNotFound("El producto ya está asignado a este empleado");
        }
        entity.setEmpleado(empleado);
        entity.setProducto(producto);
        if (dto.getEnabled() != null) {
            entity.setEnabled(dto.getEnabled());
        }
        return WhitelistProductoValueMapper.toResponse(repository.save(entity));
    }

    @Override
    @Transactional
    public WhitelistProductoResponseDTO delete(UUID id) {
        WhitelistProductoEmpleadoEntity entity = asignacionActiva(id);
        entity.setDeleted(true);
        return WhitelistProductoValueMapper.toResponse(repository.save(entity));
    }

    @Override
    @Transactional
    public WhitelistAsignacionResponseDTO asignarMasivo(WhitelistAsignacionMasivaRequestDTO dto) {
        boolean reemplazar = Boolean.TRUE.equals(dto.getReemplazar());
        EmpleadosEntity empleado = empleadoActivo(dto.getIdEmpleado());
        List<ProductoBasicoEntity> productos = productosActivos(dto.getIdProductos(), reemplazar);
        Conteos conteos = aplicar(empleado, productos, reemplazar);
        List<WhitelistProductoResponseDTO> asignaciones = repository.findByEmpleado_IdAndDeletedFalse(empleado.getId()).stream()
                .map(WhitelistProductoValueMapper::toResponse)
                .toList();
        return WhitelistAsignacionResponseDTO.builder()
                .idEmpleado(empleado.getId())
                .nombreEmpleado(WhitelistProductoValueMapper.nombreEmpleado(empleado))
                .idDepartamento(empleado.getDepartamento() == null ? null : empleado.getDepartamento().getId())
                .nombreDepartamento(empleado.getDepartamento() == null ? null : empleado.getDepartamento().getNombre())
                .empleados(1)
                .agregados(conteos.agregados)
                .yaExistian(conteos.yaExistian)
                .retirados(conteos.retirados)
                .asignaciones(asignaciones)
                .build();
    }

    @Override
    @Transactional
    public WhitelistAsignacionResponseDTO asignarDepartamento(WhitelistAsignacionDepartamentoRequestDTO dto) {
        boolean reemplazar = Boolean.TRUE.equals(dto.getReemplazar());
        DepartamentoEntity departamento = departamentoRepository.findById(dto.getIdDepartamento())
                .orElseThrow(() -> new IDNotFound("No se encontró el departamento"));
        if (departamento.isDeleted()) {
            throw new IDNotFound("No se encontró el departamento");
        }
        List<EmpleadosEntity> empleados = empleadosRepository.findByDeletedFalseAndEnabledTrueAndDepartamento_Id(departamento.getId());
        if (empleados.isEmpty()) {
            throw new IDNotFound("El departamento no tiene empleados activos");
        }
        List<ProductoBasicoEntity> productos = productosActivos(dto.getIdProductos(), reemplazar);
        Conteos total = new Conteos();
        for (EmpleadosEntity empleado : empleados) {
            total.sumar(aplicar(empleado, productos, reemplazar));
        }
        return WhitelistAsignacionResponseDTO.builder()
                .idDepartamento(departamento.getId())
                .nombreDepartamento(departamento.getNombre())
                .empleados(empleados.size())
                .agregados(total.agregados)
                .yaExistian(total.yaExistian)
                .retirados(total.retirados)
                .asignaciones(List.of())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductosPermitidosResponseDTO productosPermitidos(UUID idEmpleado) {
        EmpleadosEntity empleado = idEmpleado == null ? empleadoActual() : empleadoActivo(idEmpleado);
        boolean usarWhitelist = configuracion().isUsarWhitelist();
        List<ProductoBasicoEntity> productos = usarWhitelist
                ? repository.findActivasConProducto(empleado.getId()).stream().map(WhitelistProductoEmpleadoEntity::getProducto).toList()
                : productoRepository.findActivosOrdenados();
        return ProductosPermitidosResponseDTO.builder()
                .usarWhitelist(usarWhitelist)
                .idEmpleado(empleado.getId())
                .nombreEmpleado(WhitelistProductoValueMapper.nombreEmpleado(empleado))
                .departamento(empleado.getDepartamento() == null
                        ? null
                        : DepartamentoMapper.DepartamentoEntityToResponseSimpleDTO(empleado.getDepartamento()))
                .productos(productos.stream().map(ProductoValueMapper::ProductEntityToSimpleResponse).toList())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public WhitelistConfiguracionResponseDTO configuracion() {
        return WhitelistConfiguracionResponseDTO.builder()
                .usarWhitelist(configuracionEntity().isUsarWhitelist())
                .build();
    }

    @Override
    @Transactional
    public WhitelistConfiguracionResponseDTO actualizarConfiguracion(WhitelistConfiguracionRequestDTO dto) {
        AlmacenConfiguracionEntity configuracion = configuracionEntity();
        configuracion.setUsarWhitelist(Boolean.TRUE.equals(dto.getUsarWhitelist()));
        configuracionRepository.save(configuracion);
        return WhitelistConfiguracionResponseDTO.builder()
                .usarWhitelist(configuracion.isUsarWhitelist())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public void validarSolicitud(EmpleadosEntity empleado, Collection<UUID> productoIds) {
        if (!configuracionEntity().isUsarWhitelist()) {
            return;
        }
        if (empleado == null || empleado.getId() == null) {
            throw new IDNotFound("El empleado receptor es requerido para validar la whitelist");
        }
        if (productoIds == null || productoIds.isEmpty()) {
            return;
        }
        Set<UUID> permitidos = new HashSet<>();
        for (WhitelistProductoEmpleadoEntity asignacion : repository.findActivasConProducto(empleado.getId())) {
            permitidos.add(asignacion.getProducto().getId());
        }
        for (UUID productoId : productoIds) {
            if (productoId != null && !permitidos.contains(productoId)) {
                String nombre = productoRepository.findById(productoId)
                        .map(ProductoBasicoEntity::getNombre)
                        .orElse(productoId.toString());
                throw new IDNotFound("El empleado " + WhitelistProductoValueMapper.nombreEmpleado(empleado) + " no puede solicitar " + nombre);
            }
        }
    }

    private Conteos aplicar(EmpleadosEntity empleado, List<ProductoBasicoEntity> productos, boolean reemplazar) {
        Conteos conteos = new Conteos();
        Set<UUID> deseados = new HashSet<>();
        for (ProductoBasicoEntity producto : productos) {
            deseados.add(producto.getId());
            Optional<WhitelistProductoEmpleadoEntity> existente = repository.findFirstByEmpleado_IdAndProducto_IdAndDeletedFalse(empleado.getId(), producto.getId());
            if (existente.isPresent()) {
                WhitelistProductoEmpleadoEntity actual = existente.get();
                if (!actual.isEnabled()) {
                    actual.setEnabled(true);
                    repository.save(actual);
                    conteos.agregados++;
                } else {
                    conteos.yaExistian++;
                }
                continue;
            }
            repository.save(WhitelistProductoEmpleadoEntity.builder()
                    .empleado(empleado)
                    .producto(producto)
                    .build());
            conteos.agregados++;
        }
        if (reemplazar) {
            for (WhitelistProductoEmpleadoEntity actual : repository.findByEmpleado_IdAndDeletedFalse(empleado.getId())) {
                if (actual.getProducto() != null && !deseados.contains(actual.getProducto().getId())) {
                    actual.setDeleted(true);
                    repository.save(actual);
                    conteos.retirados++;
                }
            }
        }
        return conteos;
    }

    private List<ProductoBasicoEntity> productosActivos(List<UUID> ids, boolean permiteVacio) {
        List<UUID> distintos = ids == null
                ? List.of()
                : ids.stream().filter(Objects::nonNull).distinct().toList();
        if (distintos.isEmpty() && !permiteVacio) {
            throw new IDNotFound("Debe indicar al menos un producto");
        }
        List<ProductoBasicoEntity> productos = new ArrayList<>();
        for (UUID id : distintos) {
            productos.add(productoActivo(id));
        }
        return productos;
    }

    private WhitelistProductoEmpleadoEntity asignacionActiva(UUID id) {
        WhitelistProductoEmpleadoEntity entity = repository.findById(id)
                .orElseThrow(() -> new IDNotFound("No se encontró la asignación"));
        if (entity.isDeleted()) {
            throw new IDNotFound("No se encontró la asignación");
        }
        return entity;
    }

    private EmpleadosEntity empleadoActivo(UUID id) {
        EmpleadosEntity empleado = empleadosRepository.findById(id)
                .orElseThrow(() -> new IDNotFound("No se encontró el empleado"));
        if (empleado.isDeleted() || !empleado.isEnabled()) {
            throw new IDNotFound("El empleado no está activo");
        }
        return empleado;
    }

    private EmpleadosEntity empleadoActual() {
        EmpleadosEntity sesion = EmpleadoContext.getEmpleadoActual();
        if (sesion == null || sesion.getId() == null) {
            throw new IDNotFound("No hay un empleado autenticado");
        }
        return empleadoActivo(sesion.getId());
    }

    private ProductoBasicoEntity productoActivo(UUID id) {
        ProductoBasicoEntity producto = productoRepository.findById(id)
                .orElseThrow(() -> new IDNotFound("No se encontró el producto"));
        if (producto.isDeleted() || !producto.isEnabled()) {
            throw new IDNotFound("El producto " + producto.getNombre() + " no está disponible");
        }
        return producto;
    }

    private AlmacenConfiguracionEntity configuracionEntity() {
        return configuracionRepository.findById(AlmacenConfiguracionEntity.ID_GLOBAL)
                .orElseGet(this::crearConfiguracion);
    }

    private AlmacenConfiguracionEntity crearConfiguracion() {
        AlmacenConfiguracionEntity configuracion = AlmacenConfiguracionEntity.builder()
                .id(AlmacenConfiguracionEntity.ID_GLOBAL)
                .usarWhitelist(false)
                .build();
        return configuracionRepository.save(configuracion);
    }

    private static final class Conteos {
        private int agregados;
        private int yaExistian;
        private int retirados;

        private void sumar(Conteos otro) {
            agregados += otro.agregados;
            yaExistian += otro.yaExistian;
            retirados += otro.retirados;
        }
    }
}
