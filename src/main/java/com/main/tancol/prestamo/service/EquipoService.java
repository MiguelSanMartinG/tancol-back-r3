package com.main.tancol.prestamo.service;

import com.main.tancol.cliente.entities.ClienteEntity;
import com.main.tancol.common.exeptions.IDNotFound;
import com.main.tancol.prestamo.dto.request.EquipoRequestDTO;
import com.main.tancol.prestamo.dto.response.EquipoResponseDTO;
import com.main.tancol.prestamo.entities.EquipoAsignacionEntity;
import com.main.tancol.prestamo.entities.EquipoEntity;
import com.main.tancol.prestamo.entities.TipoEquipoEntity;
import com.main.tancol.prestamo.repositories.EquipoAsignacionRepository;
import com.main.tancol.prestamo.repositories.EquipoRepository;
import com.main.tancol.prestamo.repositories.TipoEquipoRepository;
import com.main.tancol.prestamo.service.interfaces.IEquipoService;
import com.main.tancol.prestamo.util.EquipoSpecification;
import com.main.tancol.prestamo.util.EquipoValueMapper;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EquipoService implements IEquipoService {

    private final EquipoRepository repository;
    private final TipoEquipoRepository tipoEquipoRepository;
    private final EquipoAsignacionRepository asignacionRepository;

    public EquipoService(EquipoRepository repository,
                         TipoEquipoRepository tipoEquipoRepository,
                         EquipoAsignacionRepository asignacionRepository) {
        this.repository = repository;
        this.tipoEquipoRepository = tipoEquipoRepository;
        this.asignacionRepository = asignacionRepository;
    }

    @Override
    public EquipoResponseDTO create(EquipoRequestDTO dto) {
        TipoEquipoEntity tipo = tipoEquipoRepository.findById(dto.getIdTipoEquipo())
                .orElseThrow(() -> new IDNotFound("No se encontró el tipo de equipo"));
        EquipoEntity entity = EquipoValueMapper.requestToEntity(dto, tipo);
        return EquipoValueMapper.entityToResponse(repository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public EquipoResponseDTO getById(UUID id) {
        EquipoEntity entity = repository.findById(id)
                .orElseThrow(() -> new IDNotFound("No se encontró el equipo"));
        return toResponseConCliente(entity);
    }

    @Override
    public EquipoResponseDTO update(UUID id, EquipoRequestDTO dto) {
        EquipoEntity entity = repository.findById(id)
                .orElseThrow(() -> new IDNotFound("No se encontró el equipo"));
        TipoEquipoEntity tipo = tipoEquipoRepository.findById(dto.getIdTipoEquipo())
                .orElseThrow(() -> new IDNotFound("No se encontró el tipo de equipo"));

        entity.setNombre(dto.getNombre());
        entity.setSku(dto.getSku());
        entity.setIdImagen(dto.getIdImagen());
        entity.setDescripcion(dto.getDescripcion());
        entity.setMarca(dto.getMarca());
        entity.setModelo(dto.getModelo());
        entity.setTipoEquipo(tipo);

        return toResponseConCliente(repository.save(entity));
    }

    @Override
    public EquipoResponseDTO delete(UUID id) {
        EquipoEntity entity = repository.findById(id)
                .orElseThrow(() -> new IDNotFound("No se encontró el equipo"));
        entity.setDeleted(true);
        return toResponseConCliente(repository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EquipoResponseDTO> getAll(Map<String, String> params) {
        List<EquipoEntity> equipos = repository.findAll(filterWithParameters(params));
        if (equipos.isEmpty()) {
            return Collections.emptyList();
        }

        List<UUID> ids = equipos.stream().map(EquipoEntity::getId).toList();
        Map<UUID, ClienteEntity> clientePorEquipo = asignacionRepository.findActivasByEquipoIdIn(ids).stream()
                .collect(Collectors.toMap(
                        a -> a.getEquipo().getId(),
                        EquipoAsignacionEntity::getCliente,
                        (a, b) -> a
                ));

        return equipos.stream()
                .map(e -> EquipoValueMapper.entityToResponse(e, clientePorEquipo.get(e.getId())))
                .toList();
    }

    private EquipoResponseDTO toResponseConCliente(EquipoEntity entity) {
        ClienteEntity cliente = asignacionRepository
                .findByEquipoIdAndActivaTrueAndDeletedFalse(entity.getId())
                .map(EquipoAsignacionEntity::getCliente)
                .orElse(null);
        return EquipoValueMapper.entityToResponse(entity, cliente);
    }

    private Specification<EquipoEntity> filterWithParameters(Map<String, String> params) {
        return new EquipoSpecification().getSpecificationByFilters(params);
    }
}
