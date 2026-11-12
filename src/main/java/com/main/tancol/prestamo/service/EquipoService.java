package com.main.tancol.prestamo.service;

import com.main.tancol.cliente.entities.ClienteEntity;
import com.main.tancol.common.dto.PageResponse;
import com.main.tancol.common.exeptions.IDNotFound;
import com.main.tancol.common.exceptions.exeption.FilterNotFound;
import com.main.tancol.prestamo.dto.request.EquipoRequestDTO;
import com.main.tancol.prestamo.dto.response.EquipoResponseDTO;
import com.main.tancol.prestamo.entities.EquipoAsignacionEntity;
import com.main.tancol.prestamo.entities.EquipoEntity;
import com.main.tancol.prestamo.entities.TipoEquipoEntity;
import com.main.tancol.prestamo.repositories.EquipoAsignacionRepository;
import com.main.tancol.prestamo.repositories.EquipoRepository;
import com.main.tancol.prestamo.repositories.TipoEquipoRepository;
import com.main.tancol.prestamo.service.interfaces.IEquipoService;
import com.main.tancol.prestamo.util.EquipoPageSpecification;
import com.main.tancol.prestamo.util.EquipoSpecification;
import com.main.tancol.prestamo.util.EquipoValueMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

    private static final Set<String> PAGE_KEYS = Set.of("page", "pageSize", "search");
    private static final Set<String> FILTER_KEYS = Set.of(
            "nombre", "sku", "marca", "modelo", "idTipoEquipo", "estado", "cliente", "createdAt", "updatedAt"
    );
    private static final Map<String, String> SORT_PATHS = Map.of(
            "nombre", "nombre",
            "sku", "sku",
            "marca", "marca",
            "modelo", "modelo",
            "nombreTipoEquipo", "tipoEquipo.nombre",
            "idTipoEquipo", "tipoEquipo.nombre",
            "estado", "estado",
            "createdAt", "createdAt",
            "updatedAt", "updatedAt"
    );

    @Override
    @Transactional(readOnly = true)
    public PageResponse<EquipoResponseDTO> getPage(Map<String, String> params) {
        Map<String, String> safeParams = params == null ? Map.of() : params;
        int page = positiveInt(safeParams.get("page"), 1);
        int pageSize = Math.min(100, positiveInt(safeParams.get("pageSize"), 50));
        String search = blankToNull(safeParams.get("search"));
        Map<String, String> filters = new LinkedHashMap<>();
        String sortKey = null;
        String sortDirection = null;
        for (Map.Entry<String, String> entry : safeParams.entrySet()) {
            String key = entry.getKey();
            if (PAGE_KEYS.contains(key)) {
                continue;
            }
            if (key.startsWith("filter_")) {
                String name = key.substring("filter_".length());
                if (!FILTER_KEYS.contains(name)) {
                    throw new FilterNotFound("Invalid filter: " + key);
                }
                filters.put(name, entry.getValue());
                continue;
            }
            if (key.startsWith("sort_")) {
                String name = key.substring("sort_".length());
                if (!SORT_PATHS.containsKey(name)) {
                    throw new FilterNotFound("Invalid filter: " + key);
                }
                if (sortKey == null) {
                    sortKey = name;
                    sortDirection = entry.getValue();
                }
                continue;
            }
            throw new FilterNotFound("Invalid filter: " + key);
        }
        Page<EquipoEntity> result = repository.findAll(
                new EquipoPageSpecification(search, filters),
                PageRequest.of(page - 1, pageSize, pageSort(sortKey, sortDirection))
        );
        List<EquipoResponseDTO> items = mapWithCliente(result.getContent());
        return new PageResponse<>(items, result.getTotalElements(), page, pageSize);
    }

    private List<EquipoResponseDTO> mapWithCliente(List<EquipoEntity> equipos) {
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

    private static Sort pageSort(String sortKey, String sortDirection) {
        Sort tieBreak = Sort.by(Sort.Direction.ASC, "id");
        if (sortKey == null) {
            return Sort.by(Sort.Direction.DESC, "createdAt").and(tieBreak);
        }
        String direction = sortDirection == null ? "" : sortDirection.trim();
        if (!direction.equals("ASC") && !direction.equals("DESC")) {
            throw new FilterNotFound("Invalid filter: sort_" + sortKey);
        }
        String path = SORT_PATHS.get(sortKey);
        return Sort.by(Sort.Direction.valueOf(direction), path).and(tieBreak);
    }

    private static int positiveInt(String raw, int fallback) {
        if (raw == null || raw.isBlank()) {
            return fallback;
        }
        try {
            return Math.max(1, Integer.parseInt(raw.trim()));
        } catch (NumberFormatException ex) {
            throw new FilterNotFound("Invalid filter: " + raw);
        }
    }

    private static String blankToNull(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        return raw.trim();
    }
}
