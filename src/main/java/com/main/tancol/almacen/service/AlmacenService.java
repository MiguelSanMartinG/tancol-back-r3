package com.main.tancol.almacen.service;

import com.main.tancol.common.exeptions.IDNotFound;
import com.main.tancol.auth.exceptions.exeption.PropiedaDuplicada;
import com.main.tancol.almacen.dto.request.AlmacenRequestDTO;
import com.main.tancol.almacen.dto.response.AlmacenResponseDTO;
import com.main.tancol.almacen.entities.Almacen;
import com.main.tancol.almacen.repositories.AlmacenRepository;
import com.main.tancol.almacen.service.interfaces.IAlmacenService;
import com.main.tancol.almacen.util.AlmacenSpecification;
import com.main.tancol.almacen.util.ValueMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class AlmacenService implements IAlmacenService {

    private final AlmacenRepository repository;

    public AlmacenService(AlmacenRepository almacenRepository) {
        this.repository = almacenRepository;
    }

    @Override
    public AlmacenResponseDTO createAlmacen(AlmacenRequestDTO almacen) {
        Optional<Almacen> precondicion = repository.findByNombre(almacen.getNombre());

        if (precondicion.isPresent() && !precondicion.get().isDeleted()) {
            throw new PropiedaDuplicada("Ya existe un almacén con ese nombre");
        }

        Almacen newAlmacen = Almacen.builder()
                .nombre(almacen.getNombre())
                .build();

        return ValueMapper.EntityToAlmacenResponseDTO(repository.save(newAlmacen));
    }

    @Override
    public AlmacenResponseDTO readByID(UUID id) {
        Optional<Almacen> almacen = repository.findById(id);
        if(almacen.isPresent() && !almacen.get().isDeleted()) {
            return ValueMapper.EntityToAlmacenResponseDTO(almacen.get());
        }
        throw new IDNotFound("No se encontro la id proveeida: "+id);

    }

    @Override
    public AlmacenResponseDTO updateAlmacen(UUID id, AlmacenRequestDTO almacen) {
        log.info("Actualizando Almacén con ID: {}", id);
        Almacen almacenExistente = repository.findById(id)
                .orElseThrow(() -> new IDNotFound("No se encontró el ID del almacén. ID buscada: " + id));
        // Actualizar solo los campos necesarios
        almacenExistente.setNombre(almacen.getNombre());
        almacenExistente.setEnabled(almacen.getStatus());
        Almacen almacenActualizado = repository.save(almacenExistente);
        log.info("Almacén {} actualizado correctamente", almacenActualizado.getId());

        return ValueMapper.EntityToAlmacenResponseDTO(almacenActualizado);
    }

    @Override
    public AlmacenResponseDTO deleteAlmacen(UUID id) {
        Almacen busqueda = repository.findById(id).orElse(null);
        if(busqueda != null) {
            busqueda.setDeleted(true);
            return ValueMapper.EntityToAlmacenResponseDTO(repository.save(busqueda));
        }
        throw new IDNotFound("No se encontro el ID del almacen");
    }

    @Override
    public List<AlmacenResponseDTO> getAll(Map<String, String> params) {
        List<Almacen> list = repository.findAll(filterWithParameters(params));
        log.info(list.toString());
        List<AlmacenResponseDTO> response = list.stream().map(ValueMapper::EntityToAlmacenResponseDTO).toList();
        return response;
    }

    public Specification<Almacen> filterWithParameters(Map<String, String> params) {
        return new AlmacenSpecification().getSpecificationByFilters(params);
    }

}
