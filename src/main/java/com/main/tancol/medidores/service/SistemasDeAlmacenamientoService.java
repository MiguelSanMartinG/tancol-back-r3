package com.main.tancol.medidores.service;

import com.main.tancol.common.exeptions.IDNotFound;
import com.main.tancol.empleados.entities.DepartamentoEntity;
import com.main.tancol.empleados.repositories.DepartamentoRepository;
import com.main.tancol.medidores.dto.request.SistemasDeAlmacenamientoRequestDTO;
import com.main.tancol.medidores.dto.response.SistemasDeAlmacenamientoResponseDTO;
import com.main.tancol.medidores.entities.SistemasDeAlmacenamientoEntity;
import com.main.tancol.medidores.repositories.SistemasDeAlmacenamientoRepository;
import com.main.tancol.medidores.service.interfaces.ISistemasDeAlmacenamientoService;
import com.main.tancol.medidores.util.SistemasDeAlmacenamientoSpecification;
import com.main.tancol.medidores.util.SistemasDeAlmacenamientoValueMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SistemasDeAlmacenamientoService implements ISistemasDeAlmacenamientoService {

    private final SistemasDeAlmacenamientoRepository repository;
    private final DepartamentoRepository departamentoRepository;


    @Override
    public SistemasDeAlmacenamientoResponseDTO create(SistemasDeAlmacenamientoRequestDTO dto) {

        DepartamentoEntity d = null;
        if(dto.getIdDepartamento() != null){
        d = departamentoRepository.findById(dto.getIdDepartamento())
                .orElseThrow(() -> new IDNotFound("ID No encontrada"));
        }

        SistemasDeAlmacenamientoEntity sa = SistemasDeAlmacenamientoValueMapper.SistemasDeAlmacenamientoRequestToEntity(dto, d);
        sa = repository.save(sa);
        return SistemasDeAlmacenamientoValueMapper.SistemasDeAlmacenamientoEntityToResponse(sa);
    }

    @Override
    public SistemasDeAlmacenamientoResponseDTO getById(UUID id) {
        SistemasDeAlmacenamientoEntity sa = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("No se encontro el id " + id));
        return SistemasDeAlmacenamientoValueMapper.SistemasDeAlmacenamientoEntityToResponse(sa);
    }

    @Override
    public SistemasDeAlmacenamientoResponseDTO update(UUID id, SistemasDeAlmacenamientoRequestDTO dto) {
        SistemasDeAlmacenamientoEntity sa = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("No se encontro el id " + id));
        DepartamentoEntity d = null;
        if(dto.getIdDepartamento() != null){
            d = departamentoRepository.findById(dto.getIdDepartamento())
                    .orElseThrow(() -> new IDNotFound("ID No encontrada"));
        }

        sa.setDepartamento(d);
        sa.setNombre(dto.getNombre());
        sa.setCapacidad(dto.getCapacidad());
        sa.setAlias(dto.getAlias());
        sa = repository.save(sa);
        return SistemasDeAlmacenamientoValueMapper.SistemasDeAlmacenamientoEntityToResponse(sa);
    }

    @Override
    public SistemasDeAlmacenamientoResponseDTO delete(UUID id) {
        SistemasDeAlmacenamientoEntity sa = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("No se encontro el id " + id));
        sa.setDeleted(true);
        sa = repository.save(sa);
        return SistemasDeAlmacenamientoValueMapper.SistemasDeAlmacenamientoEntityToResponse(sa);
    }

    @Override
    public List<SistemasDeAlmacenamientoResponseDTO> getAll(Map<String, String> params) {
        List<SistemasDeAlmacenamientoEntity> list = repository.findAll(filterWithParameters(params));
        return list.stream().map(SistemasDeAlmacenamientoValueMapper::SistemasDeAlmacenamientoEntityToResponse).toList();
    }

    public Specification<SistemasDeAlmacenamientoEntity> filterWithParameters(Map<String, String> params) {
        return new SistemasDeAlmacenamientoSpecification().getSpecificationByFilters(params);
    }
}
