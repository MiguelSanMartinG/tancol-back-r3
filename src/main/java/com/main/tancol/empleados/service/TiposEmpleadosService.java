package com.main.tancol.empleados.service;

import com.main.tancol.common.exeptions.IDNotFound;
import com.main.tancol.empleados.dto.request.TiposEmpleadosRequestDTO;
import com.main.tancol.empleados.dto.response.TiposEmpleadosResponseDTO;
import com.main.tancol.empleados.repositories.TiposEmpleadosRepository;
import com.main.tancol.empleados.util.TiposEmpleadosValueMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.data.jpa.domain.Specification;
import com.main.tancol.empleados.entities.TiposEmpleadosEntity;
import com.main.tancol.empleados.util.TiposEmpleadosSpecification;
import com.main.tancol.empleados.service.interfaces.ITiposEmpleadosService;

import java.util.*;

@Service
public class TiposEmpleadosService implements ITiposEmpleadosService {

    private final TiposEmpleadosRepository repository;

    public TiposEmpleadosService(TiposEmpleadosRepository repository) {
        this.repository = repository;
    }


    @Override
    public TiposEmpleadosResponseDTO create(TiposEmpleadosRequestDTO dto) {
        TiposEmpleadosEntity newEntity = TiposEmpleadosValueMapper.TiposEmpleadosRequestToEntity(dto);
        return TiposEmpleadosValueMapper.TiposEmpleadosEntityToResponse(repository.save(newEntity));
    }

    @Override
    public TiposEmpleadosResponseDTO getById(UUID id) {
        TiposEmpleadosEntity query = this.repository.findById(id).orElseThrow(() -> new IDNotFound("No se encontro el id"));
        return TiposEmpleadosValueMapper.TiposEmpleadosEntityToResponse(query);
    }

    @Override
    public TiposEmpleadosResponseDTO update(UUID id, TiposEmpleadosRequestDTO dto) {
        TiposEmpleadosEntity query = this.repository.findById(id).orElseThrow(() -> new IDNotFound("No se encontro el id"));
        query.setNombre(dto.getNombre());
        query.setAlias(dto.getAlias());
        query.setEnabled(dto.isEnabled());
        query = repository.save(query);
        return TiposEmpleadosValueMapper.TiposEmpleadosEntityToResponse(query);
    }

    @Override
    public TiposEmpleadosResponseDTO delete(UUID id) {
        TiposEmpleadosEntity query = this.repository.findById(id).orElseThrow(() -> new IDNotFound("No se encontro el id"));
        if(Objects.equals(query.getAlias(), "Ayudante") || Objects.equals(query.getAlias(), "Chofer")) {
            throw new IDNotFound("No se puede eliminar ya que esta protegido");
        }
        query.setDeleted(true);;
        query.getEmpleadosEntities().clear();
        return TiposEmpleadosValueMapper.TiposEmpleadosEntityToResponse(repository.save(query));
    }

    @Override
    public List<TiposEmpleadosResponseDTO> getAll(Map<String, String> params) {
        Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt"); // o el campo que necesites
//        List<OrdenEntity> list = ordenRepository.findAll(filterWithParameters(params), sort);
//        return list.stream().map((x) -> OrdenValueMapper.OrdenEntityToResponseDTO(x, x.getPartidas())).toList();
        List<TiposEmpleadosEntity> list = repository.findAll(filterWithParameters(params),sort);
        return list.stream().map(TiposEmpleadosValueMapper::TiposEmpleadosEntityToResponse).toList();
    }

    public Specification<TiposEmpleadosEntity> filterWithParameters(Map<String, String> params) {
        return new TiposEmpleadosSpecification().getSpecificationByFilters(params);
    }
}
