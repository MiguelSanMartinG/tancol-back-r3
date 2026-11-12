package com.main.tancol.almacen.service;

import com.main.tancol.almacen.dto.request.partidas.PartidaTransferenciaRequestDTO;
import com.main.tancol.almacen.dto.response.partidas.PartidaTransferenciaResponseDTO;
import com.main.tancol.almacen.entities.AlmacenEntity;
import com.main.tancol.almacen.repositories.PartidaTransferenciaRepository;
import com.main.tancol.almacen.util.PartidaTransferenciaValueMapper;
import com.main.tancol.empleados.entities.EmpleadosEntity;
import com.main.tancol.producto.entities.insumos.ProductoBasicoEntity;
import org.springframework.stereotype.Service;
import org.springframework.data.jpa.domain.Specification;
import com.main.tancol.almacen.entities.partidas.PartidaTransferenciaEntity;
import com.main.tancol.almacen.util.PartidaTransferenciaSpecification;
import com.main.tancol.almacen.service.interfaces.IPartidaTransferenciaService;

import java.util.*;

@Service
public class PartidaTransferenciaService implements IPartidaTransferenciaService {

    private final PartidaTransferenciaRepository repository;

    public PartidaTransferenciaService(PartidaTransferenciaRepository repository) {
        this.repository = repository;
    }


    @Override
    public PartidaTransferenciaResponseDTO create(PartidaTransferenciaRequestDTO dto) {
        return null;
    }

    @Override
    public PartidaTransferenciaResponseDTO getById(UUID id) {
        return null;
    }

    @Override
    public PartidaTransferenciaResponseDTO update(UUID id, PartidaTransferenciaRequestDTO dto) {
        return null;
    }

    @Override
    public PartidaTransferenciaResponseDTO delete(UUID id) {
        return null;
    }

    @Override
    public List<PartidaTransferenciaResponseDTO> getAll(Map<String, String> params) {
        List<PartidaTransferenciaEntity> list = repository.findAll(filterWithParameters(params));
        return list.stream().map(PartidaTransferenciaValueMapper::PartidaTransferenciaEntityToResponse).toList();
    }

    public Specification<PartidaTransferenciaEntity> filterWithParameters(Map<String, String> params) {
        return new PartidaTransferenciaSpecification().getSpecificationByFilters(params);
    }

//    public PartidaTransferenciaResponseDTO registrarTransferencia(PartidaTransferenciaRequestDTO dto, AlmacenEntity origne, AlmacenEntity destino, ProductoBasicoEntity productoBasicoEntity, EmpleadosEntity empleado) {
//       return PartidaTransferenciaValueMapper.PartidaTransferenciaEntityToResponse(
//               this.repository.save(
//                       PartidaTransferenciaValueMapper.PartidaTransferenciaRequestToEntity(dto, origne, destino, productoBasicoEntity, empleado)
//               ));
//    }
}
