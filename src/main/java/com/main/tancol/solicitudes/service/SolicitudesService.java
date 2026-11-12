package com.main.tancol.solicitudes.service;

import com.main.tancol.solicitudes.dto.request.SolicitudesRequestDTO;
import com.main.tancol.solicitudes.dto.response.SolicitudesResponseDTO;
import com.main.tancol.solicitudes.entities.SolicitudEntity;
import com.main.tancol.solicitudes.repositories.SolicitudesRepository;
import com.main.tancol.solicitudes.service.interfaces.ISolicitudesService;
import com.main.tancol.solicitudes.util.SolicitudesSpecification;
import com.main.tancol.solicitudes.util.SolicitudesValueMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SolicitudesService implements ISolicitudesService {

    private final SolicitudesRepository repository;


    @Override
    public SolicitudesResponseDTO create(SolicitudesRequestDTO dto) {
        return null;
    }

    @Override
    public SolicitudesResponseDTO getById(UUID id) {
        return null;
    }

    @Override
    public SolicitudesResponseDTO update(UUID id, SolicitudesRequestDTO dto) {
        return null;
    }

    @Override
    public SolicitudesResponseDTO delete(UUID id) {
        return null;
    }

    @Override
    public List<SolicitudesResponseDTO> getAll(Map<String, String> params) {
        List<SolicitudEntity> list = repository.findAll(filterWithParameters(params));
        return list.stream().map(SolicitudesValueMapper::SolicitudesEntityToResponse).toList();
    }

    public Specification<SolicitudEntity> filterWithParameters(Map<String, String> params) {
        return new SolicitudesSpecification().getSpecificationByFilters(params);
    }
}
