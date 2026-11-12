package com.main.tancol.proveedor.service;

import com.main.tancol.common.exeptions.IDNotFound;
import com.main.tancol.proveedor.dto.request.ProveedorRequestDTO;
import com.main.tancol.proveedor.dto.response.ProveedorResponseDTO;
import com.main.tancol.proveedor.entity.ProveedorEntity;
import com.main.tancol.proveedor.repositories.ImpuestosRepository;
import com.main.tancol.proveedor.repositories.ProveedorRepository;
import com.main.tancol.proveedor.service.interfaces.IProveedorService;
import com.main.tancol.proveedor.util.ProveedorSpecification;
import com.main.tancol.proveedor.util.ValueMapper;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProveedorService implements IProveedorService {
    private final ProveedorRepository repository;


    public ProveedorService(ProveedorRepository proveedorRepository, ImpuestosRepository ImpuestoRepository) {
        this.repository = proveedorRepository;

    }


    @Override
    public ProveedorResponseDTO create(ProveedorRequestDTO dto) {


        ProveedorEntity newProvedor =  ValueMapper.ProveedorRequestToEntity(dto);

        return ValueMapper.ProveedorEntityToResponse(repository.save(newProvedor));
    }

    @Override
    public List<ProveedorResponseDTO> getAll(Map<String, String> params) {
        List<ProveedorEntity> list = repository.findAll(filterWithParameters(params));
        List<ProveedorResponseDTO> response = list.stream().map(ValueMapper::ProveedorEntityToResponse).toList();
        return response;
    }

    @Override
    public ProveedorResponseDTO getById(UUID id) {
        return ValueMapper.ProveedorEntityToResponse(this.getEntityById(id));
    }

    public ProveedorEntity getEntityById(UUID id) {
        Optional<ProveedorEntity> busqueda = repository.findById(id);
        if(busqueda.isPresent() && !busqueda.get().isDeleted()) {
            return busqueda.get();
        }
        throw new IDNotFound("No se encontro la id de proveedor: "+ id);
    }


    @Override
    public ProveedorResponseDTO update(UUID id, ProveedorRequestDTO dto) {
        ProveedorEntity proveedorEntity = repository.findById(id)
                .orElseThrow(() -> new IDNotFound("No se encontró el ID de el proveedor con. ID buscada: " + id));

        proveedorEntity.setNombre(dto.getNombre());
        proveedorEntity.setRazonSocial(dto.getRazonSocial());
        proveedorEntity.setEnabled(dto.isEnabled());
//        proveedor.setCategoriaProducto(this.categoriaProductoRepository.findById(dto.getCategoriaProducto()).orElseThrow( () -> new IDNotFound("Id de categoria no valido.")));
        ProveedorEntity dbProveedorEntity = repository.save(proveedorEntity);

        return ValueMapper.ProveedorEntityToResponse(dbProveedorEntity);
    }

    @Override
    public ProveedorResponseDTO delete(UUID dto) {
        ProveedorEntity busqueda = repository.findById(dto).orElseThrow(
                () -> new IDNotFound("No se encontro el ID del almacen")
        );
        busqueda.setDeleted(true);
        return ValueMapper.ProveedorEntityToResponse(repository.save(busqueda));
    }

    public Specification<ProveedorEntity> filterWithParameters(Map<String, String> params) {
        return new ProveedorSpecification().getSpecificationByFilters(params);
    }
}
