package com.main.tancol.almacen.service;

import com.main.tancol.almacen.dto.request.AlmacenRequestDTO;
import com.main.tancol.almacen.dto.response.AlmacenResponseDTO;
import com.main.tancol.almacen.entities.AlmacenEntity;
import com.main.tancol.almacen.repositories.AlmacenRepository;
import com.main.tancol.almacen.service.interfaces.IAlmacenService;
import com.main.tancol.almacen.util.AlmacenSpecification;
import com.main.tancol.common.exceptions.exeption.PropiedaDuplicada;
import com.main.tancol.common.exeptions.IDNotFound;
import com.main.tancol.empleados.entities.EmpleadosEntity;
import com.main.tancol.empleados.helpers.EmpleadoContext;
import com.main.tancol.empleados.util.DepartamentoMapper;
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
        Optional<AlmacenEntity> precondicion = repository.findByNombre(almacen.getNombre());

        if (precondicion.isPresent() && !precondicion.get().isDeleted()) {
            throw new PropiedaDuplicada("Ya existe un almacén con ese nombre");
        }

        AlmacenEntity newAlmacen = AlmacenEntity.builder()
                .nombre(almacen.getNombre())
                .build();

        return DepartamentoMapper.EntityToAlmacenResponseDTO(repository.save(newAlmacen));
    }

    @Override
    public AlmacenResponseDTO readByID(UUID id) {
        Optional<AlmacenEntity> almacen = repository.findById(id);
        if(almacen.isPresent() && !almacen.get().isDeleted()) {
            return DepartamentoMapper.EntityToAlmacenResponseDTO(almacen.get());
        }
        throw new IDNotFound("No se encontro la id proveeida: "+id);

    }

    @Override
    public AlmacenResponseDTO updateAlmacen(UUID id, AlmacenRequestDTO almacen) {
        AlmacenEntity almacenExistente = repository.findById(id)
                .orElseThrow(() -> new IDNotFound("No se encontró el ID del almacén. ID buscada: " + id));
        // Actualizar solo los campos necesarios
        almacenExistente.setNombre(almacen.getNombre());
        almacenExistente.setEnabled(almacen.getStatus());
        AlmacenEntity almacenActualizado = repository.save(almacenExistente);
        return DepartamentoMapper.EntityToAlmacenResponseDTO(almacenActualizado);
    }

    @Override
    public AlmacenResponseDTO deleteAlmacen(UUID id) {
        AlmacenEntity busqueda = repository.findById(id).orElse(null);
        if(busqueda != null) {
            busqueda.setDeleted(true);
            return DepartamentoMapper.EntityToAlmacenResponseDTO(repository.save(busqueda));
        }
        throw new IDNotFound("No se encontro el ID del almacen");
    }

    @Override
    public List<AlmacenResponseDTO> getAll(Map<String, String> params) {
        List<AlmacenEntity> list = repository.findAll(filterWithParameters(params));
        List<AlmacenResponseDTO> response = list.stream().map(DepartamentoMapper::EntityToAlmacenResponseDTO).toList();

        EmpleadosEntity emp = EmpleadoContext.getEmpleadoActual();

        Long idEmpleado = emp.getIdEmpleado();
        String usuario = emp.getNombreUsuario();


        return response;
    }

    public Specification<AlmacenEntity> filterWithParameters(Map<String, String> params) {
        return new AlmacenSpecification().getSpecificationByFilters(params);
    }

}
