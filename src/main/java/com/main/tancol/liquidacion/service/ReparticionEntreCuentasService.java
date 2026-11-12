package com.main.tancol.liquidacion.service;

import com.main.tancol.empleados.entities.EmpleadosEntity;
import com.main.tancol.empleados.helpers.EmpleadoContext;
import com.main.tancol.liquidacion.dto.request.ReparticionEntreCuentasRequestDTO;
import com.main.tancol.liquidacion.dto.response.ReparticionEntreCuentasResponseDTO;
import com.main.tancol.liquidacion.entities.ReparticionEntreCuentasEntity;
import com.main.tancol.liquidacion.repositories.ReparticionEntreCuentasRepository;
import com.main.tancol.liquidacion.service.interfaces.IReparticionEntreCuentasService;
import com.main.tancol.liquidacion.util.ReparticionEntreCuentasSpecification;
import com.main.tancol.liquidacion.util.ReparticionEntreCuentasValueMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReparticionEntreCuentasService implements IReparticionEntreCuentasService {

    private final ReparticionEntreCuentasRepository repository;


    @Override
    public ReparticionEntreCuentasResponseDTO create(ReparticionEntreCuentasRequestDTO dto) {
        EmpleadosEntity emp = EmpleadoContext.getEmpleadoActual();

        if (ObjectUtils.isEmpty(emp)) {
            throw new IllegalArgumentException("No se encontro el usuario");
        }
        ReparticionEntreCuentasEntity rp = ReparticionEntreCuentasEntity.builder()
                .cuentaA(dto.getCuentaA())
                .cuentaB(dto.getCuentaB())
                .cuentaC(dto.getCuentaC())
                .usuarioQueEdito(emp.getNombreUsuario()).build();

        return ReparticionEntreCuentasValueMapper.ReparticionEntreCuentasEntityToResponse(repository.save(rp));
    }

    @Override
    public ReparticionEntreCuentasResponseDTO getById(UUID id) {
        return null;
    }

    public ReparticionEntreCuentasEntity getLatest(){
        return this.repository.findTopByOrderByCreatedAtDesc();
//        return null;
    }

    @Override
    public ReparticionEntreCuentasResponseDTO update(UUID id, ReparticionEntreCuentasRequestDTO dto) {
        return null;
    }

    @Override
    public ReparticionEntreCuentasResponseDTO delete(UUID id) {
        return null;
    }

    @Override
    public List<ReparticionEntreCuentasResponseDTO> getAll(Map<String, String> params) {
        Sort sort = null;
        // sort = Sort.by(Sort.Direction.ASC, "createdAt");
        List<ReparticionEntreCuentasEntity> list = repository.findAll(filterWithParameters(params));
        return list.stream().map(ReparticionEntreCuentasValueMapper::ReparticionEntreCuentasEntityToResponse).toList();
    }

    public Specification<ReparticionEntreCuentasEntity> filterWithParameters(Map<String, String> params) {
        return new ReparticionEntreCuentasSpecification().getSpecificationByFilters(params);
    }
}
