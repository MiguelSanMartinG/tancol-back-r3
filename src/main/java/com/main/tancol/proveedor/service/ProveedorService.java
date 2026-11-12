package com.main.tancol.proveedor.service;

import com.main.tancol.common.exeptions.IDNotFound;
import com.main.tancol.proveedor.dto.request.ProveedorRequestDTO;
import com.main.tancol.proveedor.dto.response.ProveedorResponseDTO;
import com.main.tancol.proveedor.entity.ProveedorEntity;
import com.main.tancol.proveedor.entity.TipoProveedorEntity;
import com.main.tancol.proveedor.repositories.ImpuestosRepository;
import com.main.tancol.proveedor.repositories.ProveedorRepository;
import com.main.tancol.proveedor.repositories.TipoProveedorRepository;
import com.main.tancol.proveedor.service.interfaces.IProveedorService;
import com.main.tancol.proveedor.util.InformacionFiscalValueMapper;
import com.main.tancol.proveedor.util.ProveedorSpecification;
import com.main.tancol.proveedor.util.ProveedorValueMapper;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ProveedorService implements IProveedorService {
    private final ProveedorRepository repository;
    private final TipoProveedorRepository tipoProveedorRepository;


    public ProveedorService(ProveedorRepository proveedorRepository, ImpuestosRepository ImpuestoRepository, TipoProveedorRepository tipoProveedorRepository) {
        this.repository = proveedorRepository;

        this.tipoProveedorRepository = tipoProveedorRepository;
    }


    @Override
    @Transactional
    public ProveedorResponseDTO create(ProveedorRequestDTO dto) {
        ProveedorEntity newProvedor = ProveedorValueMapper.ProveedorRequestToEntity(dto);
        newProvedor.setTiposProveedor(resolverTipos(dto));

        return ProveedorValueMapper.ProveedorEntityToResponse(repository.save(newProvedor));
    }

    @Override
    @Transactional
    public List<ProveedorResponseDTO> getAll(Map<String, String> params) {
        List<ProveedorEntity> list = repository.findAll(filterWithParameters(params));
        List<ProveedorResponseDTO> response = list.stream().map(ProveedorValueMapper::ProveedorEntityToResponse).toList();
        return response;
    }

    @Override
    @Transactional
    public ProveedorResponseDTO getById(UUID id) {
        return ProveedorValueMapper.ProveedorEntityToResponse(this.getEntityById(id));
    }

    public ProveedorEntity getEntityById(UUID id) {
        Optional<ProveedorEntity> busqueda = repository.findById(id);
        if(busqueda.isPresent() && !busqueda.get().isDeleted()) {
            return busqueda.get();
        }
        throw new IDNotFound("No se encontro la id de proveedor: "+ id);
    }


    @Override
    @Transactional
    public ProveedorResponseDTO update(UUID id, ProveedorRequestDTO dto) {
        ProveedorEntity proveedorEntity = repository.findById(id)
                .orElseThrow(() -> new IDNotFound("No se encontró el ID de el proveedor con. ID buscada: " + id));
        proveedorEntity.setNombreComercial(dto.getNombreComercial());
        if (proveedorEntity.getTiposProveedor() == null) {
            proveedorEntity.setTiposProveedor(new LinkedHashSet<>());
        }
        proveedorEntity.getTiposProveedor().clear();
        proveedorEntity.getTiposProveedor().addAll(resolverTipos(dto));
        proveedorEntity.setCondicionPago(dto.getCondicionPago());
        proveedorEntity.setCorreo(dto.getCorreo());
        proveedorEntity.setTelefono(dto.getTelefono());
        proveedorEntity.setInformacionFiscal(InformacionFiscalValueMapper.InformacionFiscalRequestToEntity(dto.getInformacionFiscal()));
        ProveedorEntity dbProveedorEntity = repository.save(proveedorEntity);
        return ProveedorValueMapper.ProveedorEntityToResponse(dbProveedorEntity);
    }

    @Override
    @Transactional
    public ProveedorResponseDTO delete(UUID dto) {
        ProveedorEntity busqueda = repository.findById(dto).orElseThrow(
                () -> new IDNotFound("No se encontro el ID del almacen")
        );
        busqueda.setDeleted(true);
        return ProveedorValueMapper.ProveedorEntityToResponse(repository.save(busqueda));
    }

    public Specification<ProveedorEntity> filterWithParameters(Map<String, String> params) {
        return new ProveedorSpecification().getSpecificationByFilters(params);
    }

    private Set<TipoProveedorEntity> resolverTipos(ProveedorRequestDTO dto) {
        LinkedHashSet<UUID> ids = new LinkedHashSet<>();
        if (dto.getIdsTipoProveedor() != null) {
            dto.getIdsTipoProveedor().stream().filter(Objects::nonNull).forEach(ids::add);
        } else if (dto.getIdTipoProveedor() != null) {
            ids.add(dto.getIdTipoProveedor());
        }
        if (ids.isEmpty()) {
            throw new IDNotFound("Debe indicar al menos un tipo de proveedor");
        }

        Map<UUID, TipoProveedorEntity> encontrados = StreamSupport.stream(
                        tipoProveedorRepository.findAllById(ids).spliterator(), false)
                .collect(Collectors.toMap(TipoProveedorEntity::getId, Function.identity()));
        if (encontrados.size() != ids.size()) {
            throw new IDNotFound("No existe la categoría de proveedor");
        }

        Set<TipoProveedorEntity> tipos = new LinkedHashSet<>();
        ids.forEach(id -> tipos.add(encontrados.get(id)));
        return tipos;
    }
}
