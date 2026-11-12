package com.main.tancol.producto.service;

import com.main.tancol.common.exeptions.IDNotFound;
import com.main.tancol.auth.exceptions.exeption.PropiedaDuplicada;
import com.main.tancol.producto.dto.request.ProductoRequestDTO;
import com.main.tancol.producto.dto.response.ProductoResponseDTO;
import com.main.tancol.producto.entities.CategoriaProductoEntity;
import com.main.tancol.producto.entities.ProductoEntity;
import com.main.tancol.producto.repositories.CategoriaProductoRepository;
import com.main.tancol.producto.repositories.ProductoRepository;
import com.main.tancol.producto.service.interfaces.IProductoService;

import com.main.tancol.producto.util.ProductoSpecification;
import com.main.tancol.producto.util.ProductoValueMapper;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class ProductoService implements IProductoService {
    private final ProductoRepository repository;
    private final CategoriaProductoRepository categoriaProductoRepository;

    public ProductoService(ProductoRepository productoRepository, CategoriaProductoRepository categoriaProductoRepository) {
        this.repository = productoRepository;
        this.categoriaProductoRepository = categoriaProductoRepository;
    }


    @Override
    public ProductoResponseDTO create(ProductoRequestDTO dto) {
        if(this.repository.existsProductoByNombre(dto.getNombre().trim())){
            throw new PropiedaDuplicada("El nombre ya existe");
        }
        CategoriaProductoEntity dbCat = this.categoriaProductoRepository.findById(dto.getCategoriaProductoId())
                .orElseThrow(() -> new IDNotFound("El id Categoria no existe: " + dto.getCategoriaProductoId()));

          return ProductoValueMapper.ProductEntityToResponseDTO(this.repository.save(ProductoValueMapper.ProductoRequestDTOToEntity(dto, dbCat)));

    }

    @Override
    public List<ProductoResponseDTO> getAll(Map<String, String> params) {
        List<ProductoEntity> list = repository.findAll(filterWithParameters(params));
        log.info(list.toString());
        return list.stream().map(ProductoValueMapper::ProductEntityToResponseDTO).toList();
    }

    @Override
    public ProductoResponseDTO getById(UUID id) {
        Optional<ProductoEntity> busqueda = repository.findById(id);
        if(busqueda.isPresent() && !busqueda.get().isDeleted()) {
            return ProductoValueMapper.ProductEntityToResponseDTO(busqueda.get());
        }
        throw new IDNotFound("No se encontro la id proveeida: "+ id);
    }

    @Override
    @Transactional
    public ProductoResponseDTO update(UUID id,ProductoRequestDTO dto) {

        log.info("Actualizando Producto con ID: {}", id);
        ProductoEntity productoEntity = repository.findById(id)
                .orElseThrow(() -> new IDNotFound("No se encontró el ID de el producto con. ID buscada: " + id));

        productoEntity.setNombre(dto.getNombre());
        productoEntity.setDescripcion(dto.getDescripcion());
        productoEntity.setAlias(dto.getAlias());
        productoEntity.setSku(dto.getSku());
        productoEntity.setEnabled(dto.getActivo());
        productoEntity.setCategoriaProductoEntity(this.categoriaProductoRepository.findById(dto.getCategoriaProductoId()).orElseThrow( () -> new IDNotFound("Id de categoria no valido.")));
        ProductoEntity dbProductoEntity = repository.save(productoEntity);

        log.info("Producto {} actualizado correctamente", dbProductoEntity.getId());

        return ProductoValueMapper.ProductEntityToResponseDTO(dbProductoEntity);
    }

    @Override
    public ProductoResponseDTO delete(UUID dto) {
        ProductoEntity busqueda = repository.findById(dto).orElseThrow(
                () -> new IDNotFound("No se encontro el ID del almacen")
        );
        busqueda.setDeleted(true);
        busqueda.setCategoriaProductoEntity(null);
        return ProductoValueMapper.ProductEntityToResponseDTO(repository.save(busqueda));

    }

    public Specification<ProductoEntity> filterWithParameters(Map<String, String> params) {
        return new ProductoSpecification().getSpecificationByFilters(params);
    }


}
