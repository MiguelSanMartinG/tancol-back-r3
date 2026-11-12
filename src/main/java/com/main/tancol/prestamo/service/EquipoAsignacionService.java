package com.main.tancol.prestamo.service;

import com.main.tancol.cliente.entities.ClienteEntity;
import com.main.tancol.cliente.repositories.ClienteRepository;
import com.main.tancol.common.exeptions.IDNotFound;
import com.main.tancol.prestamo.dto.request.EquipoAsignacionRequestDTO;
import com.main.tancol.prestamo.dto.response.EquipoAsignacionResponseDTO;
import com.main.tancol.prestamo.entities.EquipoAsignacionEntity;
import com.main.tancol.prestamo.entities.EquipoEntity;
import com.main.tancol.prestamo.enums.EstadoEquipo;
import com.main.tancol.prestamo.repositories.EquipoAsignacionRepository;
import com.main.tancol.prestamo.repositories.EquipoRepository;
import com.main.tancol.prestamo.service.interfaces.IEquipoAsignacionService;
import com.main.tancol.prestamo.util.EquipoAsignacionSpecification;
import com.main.tancol.prestamo.util.EquipoAsignacionValueMapper;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class EquipoAsignacionService implements IEquipoAsignacionService {

    private final EquipoAsignacionRepository repository;
    private final EquipoRepository equipoRepository;
    private final ClienteRepository clienteRepository;

    public EquipoAsignacionService(EquipoAsignacionRepository repository,
                                  EquipoRepository equipoRepository,
                                  ClienteRepository clienteRepository) {
        this.repository = repository;
        this.equipoRepository = equipoRepository;
        this.clienteRepository = clienteRepository;
    }

    @Override
    @Transactional
    public EquipoAsignacionResponseDTO create(EquipoAsignacionRequestDTO dto) {
        if (dto.getMontoMinimo() == null || dto.getMontoMinimo().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El monto mínimo es obligatorio y no puede ser negativo");
        }

        EquipoEntity equipo = equipoRepository.findById(dto.getIdEquipo())
                .orElseThrow(() -> new IDNotFound("No se encontró el equipo"));

        if (equipo.isDeleted()) {
            throw new IDNotFound("El equipo está eliminado");
        }
        if (equipo.getEstado() != EstadoEquipo.DISPONIBLE) {
            throw new IDNotFound("El equipo no está disponible para asignación");
        }
        if (repository.existsByEquipoIdAndActivaTrueAndDeletedFalse(equipo.getId())) {
            throw new IDNotFound("El equipo ya tiene una asignación activa");
        }

        ClienteEntity cliente = clienteRepository.findById(dto.getIdCliente())
                .orElseThrow(() -> new IDNotFound("No se encontró el cliente"));

        EquipoAsignacionEntity asignacion = EquipoAsignacionValueMapper.requestToEntity(dto, equipo, cliente);
        equipo.setEstado(EstadoEquipo.ASIGNADO);
        equipoRepository.save(equipo);

        return EquipoAsignacionValueMapper.entityToResponse(repository.save(asignacion));
    }

    @Override
    public EquipoAsignacionResponseDTO getById(UUID id) {
        EquipoAsignacionEntity entity = repository.findById(id)
                .orElseThrow(() -> new IDNotFound("No se encontró la asignación"));
        return EquipoAsignacionValueMapper.entityToResponse(entity);
    }

    @Override
    public List<EquipoAsignacionResponseDTO> getAll(Map<String, String> params) {
        return repository.findAll(filterWithParameters(params)).stream()
                .map(EquipoAsignacionValueMapper::entityToResponse)
                .toList();
    }

    @Override
    @Transactional
    public EquipoAsignacionResponseDTO devolver(UUID id) {
        EquipoAsignacionEntity asignacion = repository.findById(id)
                .orElseThrow(() -> new IDNotFound("No se encontró la asignación"));

        if (!asignacion.isActiva()) {
            throw new IDNotFound("La asignación ya fue cerrada");
        }

        asignacion.setActiva(false);
        asignacion.setFechaDevolucion(LocalDateTime.now());

        EquipoEntity equipo = asignacion.getEquipo();
        equipo.setEstado(EstadoEquipo.DISPONIBLE);
        equipoRepository.save(equipo);

        return EquipoAsignacionValueMapper.entityToResponse(repository.save(asignacion));
    }

    private Specification<EquipoAsignacionEntity> filterWithParameters(Map<String, String> params) {
        return new EquipoAsignacionSpecification().getSpecificationByFilters(params);
    }
}
