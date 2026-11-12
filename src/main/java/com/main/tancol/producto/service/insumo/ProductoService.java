package com.main.tancol.producto.service.insumo;

import com.main.tancol.almacen.entities.UnidadesMedidaEntity;
import com.main.tancol.almacen.repositories.UnidadesMedidaRepository;
import com.main.tancol.common.exceptions.exeption.GenericExeption;
import com.main.tancol.common.exceptions.exeption.PropiedaDuplicada;
import com.main.tancol.common.exeptions.IDNotFound;
import com.main.tancol.notificacion.service.NotificacionService;
import com.main.tancol.producto.dto.request.insumo.ProductoRequestDTO;
import com.main.tancol.producto.dto.response.insumos.ProductoResponseDTO;
import com.main.tancol.producto.entities.insumos.CategoriaProductoEntity;
import com.main.tancol.producto.entities.insumos.ProductoBasicoEntity;
import com.main.tancol.producto.repositories.CategoriaProductoRepository;
import com.main.tancol.producto.repositories.ProductoRepository;
import com.main.tancol.producto.service.interfaces.IProductoService;
import com.main.tancol.producto.util.insumos.ProductoSpecification;
import com.main.tancol.producto.util.insumos.ProductoValueMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductoService implements IProductoService {

    private final UnidadesMedidaRepository unidadesMedidaRepository;
    private final ProductoRepository repository;
    private final CategoriaProductoRepository categoriaProductoRepository;
    private final ModelMapper modelMapper;
    private final NotificacionService notificacionService;

    @Override
    public ProductoResponseDTO create(ProductoRequestDTO dto) {
        validarStock(dto.getStockMinimo(), dto.getStockMaximo(), dto.getNotificarStockMinimo());
        validarVigencia(dto.getTieneVigencia(), dto.getFechaVigencia());
        if(this.repository.existsProductoByNombre(dto.getNombre().trim())){
            throw new PropiedaDuplicada("El nombre ya existe");
        }

        UnidadesMedidaEntity unidadesMedida = null;
        if(dto.getUnidadMedidaDefault() != null){
        unidadesMedida = unidadesMedidaRepository.findById(dto.getUnidadMedidaDefault()).orElseThrow(()-> new IDNotFound("No existe unidad medida"));
        }

        CategoriaProductoEntity dbCat = this.categoriaProductoRepository.findById(dto.getCategoriaProductoId())
                .orElseThrow(() -> new IDNotFound("El id Categoria no existe: " + dto.getCategoriaProductoId()));

          return ProductoValueMapper.ProductEntityToResponseDTO(this.repository.save(ProductoValueMapper.ProductoRequestDTOToEntity(dto, dbCat, unidadesMedida)));

    }

    @Override
    public List<ProductoResponseDTO> getAll(Map<String, String> params) {
        List<ProductoBasicoEntity> list = repository.findAll(filterWithParameters(params));
        return list.stream().map(ProductoValueMapper::ProductEntityToResponseDTO).toList();
    }

    @Override
    public ProductoResponseDTO getById(UUID id) {
        Optional<ProductoBasicoEntity> busqueda = repository.findById(id);
        if(busqueda.isPresent() && !busqueda.get().isDeleted()) {
            return ProductoValueMapper.ProductEntityToResponseDTO(busqueda.get());
        }
        throw new IDNotFound("No se encontro la id proveeida: "+ id);
    }

    @Override
    @Transactional
    public ProductoResponseDTO update(UUID id,ProductoRequestDTO dto) {
        validarStock(dto.getStockMinimo(), dto.getStockMaximo(), dto.getNotificarStockMinimo());
        validarVigencia(dto.getTieneVigencia(), dto.getFechaVigencia());

        ProductoBasicoEntity productoBasicoEntity = repository.findById(id)
                .orElseThrow(() -> new IDNotFound("No se encontró el ID de el producto con. ID buscada: " + id));
        boolean desactivarNotificacion = productoBasicoEntity.isNotificarStockMinimo()
                && !Boolean.TRUE.equals(dto.getNotificarStockMinimo());

        UnidadesMedidaEntity unidadesMedida = null;
        if(dto.getUnidadMedidaDefault() != null){
            unidadesMedida = unidadesMedidaRepository.findById(dto.getUnidadMedidaDefault()).orElseThrow(()-> new IDNotFound("No existe unidad medida"));
        }

        productoBasicoEntity.setNombre(dto.getNombre());
        productoBasicoEntity.setDescripcion(dto.getDescripcion());
        productoBasicoEntity.setAlias(dto.getAlias());
        productoBasicoEntity.setSku(dto.getSku());
        productoBasicoEntity.setIdImagen(dto.getIdImagen());
        productoBasicoEntity.setMarca(dto.getMarca());
        productoBasicoEntity.setModelo(dto.getModelo());
        productoBasicoEntity.setStockMinimo(dto.getStockMinimo());
        productoBasicoEntity.setStockMaximo(dto.getStockMaximo());
        productoBasicoEntity.setNotificarStockMinimo(Boolean.TRUE.equals(dto.getNotificarStockMinimo()));
        productoBasicoEntity.setTieneVigencia(Boolean.TRUE.equals(dto.getTieneVigencia()));
        productoBasicoEntity.setFechaVigencia(Boolean.TRUE.equals(dto.getTieneVigencia()) ? dto.getFechaVigencia() : null);
        productoBasicoEntity.setCategoriaProductoEntity(this.categoriaProductoRepository.findById(dto.getCategoriaProductoId()).orElseThrow( () -> new IDNotFound("Id de categoria no valido.")));
        productoBasicoEntity.setUnidadMedidaDefault(unidadesMedida);
        ProductoBasicoEntity dbProductoBasicoEntity = repository.save(productoBasicoEntity);
        if (desactivarNotificacion) {
            notificacionService.desactivarStockMinimo(id);
        }

        log.info("Producto {} actualizado correctamente", dbProductoBasicoEntity.getId());

        return ProductoValueMapper.ProductEntityToResponseDTO(dbProductoBasicoEntity);
    }

    @Override
    @Transactional
    public ProductoResponseDTO delete(UUID dto) {
        ProductoBasicoEntity busqueda = repository.findById(dto).orElseThrow(
                () -> new IDNotFound("No se encontro el ID del producto")
        );
        busqueda.setDeleted(true);
        busqueda.setNotificarStockMinimo(false);
        busqueda.setCategoriaProductoEntity(null);
        notificacionService.desactivarStockMinimo(dto);
        return ProductoValueMapper.ProductEntityToResponseDTO(repository.save(busqueda));

    }

    private void validarStock(BigDecimal stockMinimo, BigDecimal stockMaximo, Boolean notificarStockMinimo) {
        if (stockMinimo != null && stockMinimo.signum() < 0) {
            throw new GenericExeption("El stock mínimo no puede ser negativo");
        }
        if (stockMaximo != null && stockMaximo.signum() < 0) {
            throw new GenericExeption("El stock máximo no puede ser negativo");
        }
        if (stockMinimo != null && stockMaximo != null && stockMaximo.compareTo(stockMinimo) < 0) {
            throw new GenericExeption("El stock máximo debe ser mayor o igual al stock mínimo");
        }
        if (Boolean.TRUE.equals(notificarStockMinimo) && stockMinimo == null) {
            throw new GenericExeption("Para activar la notificación debe indicar el stock mínimo");
        }
    }

    private void validarVigencia(Boolean tieneVigencia, LocalDate fechaVigencia) {
        if (Boolean.TRUE.equals(tieneVigencia) && fechaVigencia == null) {
            throw new GenericExeption("Si el insumo tiene vigencia debe indicar la fecha de vigencia");
        }
        if (!Boolean.TRUE.equals(tieneVigencia) && fechaVigencia != null) {
            throw new GenericExeption("La fecha de vigencia solo aplica cuando el insumo tiene vigencia");
        }
    }

    public Specification<ProductoBasicoEntity> filterWithParameters(Map<String, String> params) {
        return new ProductoSpecification().getSpecificationByFilters(params);
    }

}
