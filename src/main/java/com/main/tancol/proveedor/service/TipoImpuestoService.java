package com.main.tancol.proveedor.service;

import com.main.tancol.common.exeptions.IDNotFound;
import com.main.tancol.proveedor.dto.request.TipoImpuestoRequestDTO;
import com.main.tancol.proveedor.dto.response.TipoImpuestoResponseDTO;
import com.main.tancol.proveedor.entity.TipoImpuesto;
import com.main.tancol.proveedor.repositories.ImpuestosRepository;
import com.main.tancol.proveedor.service.interfaces.ITipoImpuestoService;
import com.main.tancol.proveedor.util.TipoImpuestoSpecification;
import com.main.tancol.proveedor.util.ValueMapper;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class TipoImpuestoService implements ITipoImpuestoService {

    private final ImpuestosRepository repository;

    public TipoImpuestoService(ImpuestosRepository repository) {
        this.repository = repository;
    }

    @Override
    public TipoImpuestoResponseDTO create(TipoImpuestoRequestDTO dto) {
        TipoImpuesto newImpuesto = ValueMapper.TipoImpuestoRequestToEntity(dto);
        return ValueMapper.TipoImpuestoEntityToResponse(repository.save(newImpuesto));
    }

    @Override
    public List<TipoImpuestoResponseDTO> getAll(Map<String, String> params) {
        List<TipoImpuesto> list = repository.findAll(filterWithParameters(params));
        List<TipoImpuestoResponseDTO> response = list.stream().map(ValueMapper::TipoImpuestoEntityToResponse).toList();
        return response;
    }

    @Override
    public TipoImpuestoResponseDTO getById(UUID id) {
        Optional<TipoImpuesto> busqueda = repository.findById(id);
        if (busqueda.isPresent() && !busqueda.get().isDeleted()) {
            return ValueMapper.TipoImpuestoEntityToResponse(busqueda.get());
        }
        throw new IDNotFound("No se encontro la id de proveedor: " + id);
    }

    @Override
    public TipoImpuestoResponseDTO update(UUID id, TipoImpuestoRequestDTO dto) {
        TipoImpuesto timpuesto = repository.findById(id)
                .orElseThrow(() -> new IDNotFound("No se encontró el ID de el proveedor con. ID buscada: " + id));

        timpuesto.setNombre(dto.getNombre());
        timpuesto.setPorcentaje(dto.getPorcentaje());
//        timpuesto.setCategoriaProducto(this.categoriaProductoRepository.findById(dto.getCategoriaProducto()).orElseThrow( () -> new IDNotFound("Id de categoria no valido.")));
        TipoImpuesto dbProveedor = repository.save(timpuesto);

        return ValueMapper.TipoImpuestoEntityToResponse(dbProveedor);
    }

    @Override
    @Transactional
    public TipoImpuestoResponseDTO delete(UUID dto) {
        TipoImpuesto busqueda = repository.findById(dto).orElseThrow(
                () -> new IDNotFound("No se encontro el ID del almacen")
        );
        busqueda.setDeleted(true);
        return ValueMapper.TipoImpuestoEntityToResponse(repository.save(busqueda));
    }

    public Specification<TipoImpuesto> filterWithParameters(Map<String, String> params) {
        return new TipoImpuestoSpecification().getSpecificationByFilters(params);
    }
}
