package com.main.tancol.producto.service;

import com.main.tancol.common.exeptions.IDNotFound;
import com.main.tancol.producto.dto.request.CategoriaRequestDTO;
import com.main.tancol.producto.dto.response.CategoriaResponseDTO;
import com.main.tancol.producto.entities.CategoriaProductoEntity;
import com.main.tancol.producto.repositories.CategoriaProductoRepository;
import com.main.tancol.almacen.service.interfaces.ICategoriaProductoService;
import com.main.tancol.almacen.util.ValueMapper;
import com.main.tancol.producto.util.CateogriaSpecification;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class CategoriaService implements ICategoriaProductoService {
    private final CategoriaProductoRepository repository;

    public CategoriaService(CategoriaProductoRepository categoriaProductoRepository) {
        this.repository = categoriaProductoRepository;
    }


    @Override
    public CategoriaResponseDTO createCategoria(CategoriaRequestDTO dto) {
//        Optional<CategoriaProducto> precondicion = repository.findByNombre(dto.getNombre());
//
//        if (precondicion.isPresent() && !precondicion.get().isDeleted()) {
//            throw new PropiedaDuplicada("Ya existe una categoria con ese nombre");
//        }

        CategoriaProductoEntity nwCategoria = CategoriaProductoEntity.builder()
                .nombre(dto.getNombre())
                .build();
        nwCategoria.setEnabled(true);

        return ValueMapper.EntityToCategoriaProductoResponseDTO(repository.save(nwCategoria));
    }

    @Override
    public CategoriaResponseDTO readByID(UUID id) {
        Optional<CategoriaProductoEntity> busqueda = repository.findById(id);
        if(busqueda.isPresent() && !busqueda.get().isDeleted()) {
            return ValueMapper.EntityToCategoriaProductoResponseDTO(busqueda.get());
        }
        throw new IDNotFound("No se encontro la id proveeida: "+ id);
    }

    @Override
    @Operation(
            summary = "No se actualiza la lista"
    )
    public CategoriaResponseDTO updateCategoria(UUID id, CategoriaRequestDTO dto) {
        log.info("Actualizando Categoria con ID: {}", id);
        CategoriaProductoEntity categoria = repository.findById(id)
                .orElseThrow(() -> new IDNotFound("No se encontró el ID de la categoria. ID buscada: " + id));
        // Actualizar solo los campos necesarios
        categoria.setNombre(dto.getNombre());
        categoria.setEnabled(dto.getEnabled());
        CategoriaProductoEntity categoriaDB = repository.save(categoria);
        log.info("Almacén {} actualizado correctamente", categoriaDB.getId());

        return ValueMapper.EntityToCategoriaProductoResponseDTO(categoriaDB);
    }

    @Override
    public CategoriaResponseDTO deleteCategoria(UUID id) {
        CategoriaProductoEntity busqueda = repository.findById(id).orElse(null);
        if(busqueda != null) {
            busqueda.setDeleted(true);
            return ValueMapper.EntityToCategoriaProductoResponseDTO(repository.save(busqueda));
        }
        throw new IDNotFound("No se encontro el ID del almacen");
    }

    @Override
    public List<CategoriaResponseDTO> getAll(Map<String, String> params) {
        List<CategoriaProductoEntity> list = repository.findAll(filterWithParameters(params));
        log.info(list.toString());
        List<CategoriaResponseDTO> response = list.stream().map(ValueMapper::EntityToCategoriaProductoResponseDTO).toList();
        return response;
    }

    public Specification<CategoriaProductoEntity> filterWithParameters(Map<String, String> params) {
        return new CateogriaSpecification().getSpecificationByFilters(params);
    }


}
