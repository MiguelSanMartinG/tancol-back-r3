package com.main.tancol.logistica.service.rutas;

import com.main.tancol.almacen.entities.UnidadesMedidaEntity;
import com.main.tancol.almacen.repositories.UnidadesMedidaRepository;
import com.main.tancol.common.exeptions.IDNotFound;
import com.main.tancol.logistica.dto.request.VehiculosRequestDTO;
import com.main.tancol.logistica.dto.response.VehiculosResponseDTO;
import com.main.tancol.logistica.entities.rutas.VehiculosEntity;
import com.main.tancol.logistica.repositories.VehiculosRepository;
import com.main.tancol.logistica.service.interfaces.IVehiculosService;
import com.main.tancol.logistica.util.VehiculosSpecification;
import com.main.tancol.logistica.util.VehiculosValueMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VehiculosService implements IVehiculosService {

    private final VehiculosRepository repository;
    private final UnidadesMedidaRepository unidadesMedidaRepository;


    @Override
    public VehiculosResponseDTO create(VehiculosRequestDTO dto) {
        UnidadesMedidaEntity unidad = unidadesMedidaRepository.findById(dto.getUnidadMedida()).orElseThrow(()-> new IDNotFound("No se encontro unidad de medida"));
        VehiculosEntity vehiculosEntity = VehiculosValueMapper.VehiculosRequestToEntity(dto, unidad);
        return VehiculosValueMapper.VehiculosEntityToResponse(repository.save(vehiculosEntity));
    }

    @Override
    public VehiculosResponseDTO getById(UUID id) {
        VehiculosEntity vehiculosEntity = repository.findById(id).orElseThrow(()-> new IDNotFound("No se encontro el vehiculo"));
        return VehiculosValueMapper.VehiculosEntityToResponse(vehiculosEntity);
    }

    @Override
    public VehiculosResponseDTO update(UUID id, VehiculosRequestDTO dto) {
        UnidadesMedidaEntity unidad = unidadesMedidaRepository.findById(dto.getUnidadMedida()).orElseThrow(()-> new IDNotFound("No se encontro unidad de medida"));
        VehiculosEntity vehiculosEntity = repository.findById(id).orElseThrow(()-> new IDNotFound("No se encontro el vehiculo"));
        vehiculosEntity.setNombre(dto.getNombre());
        vehiculosEntity.setCapacidadMaxima(dto.getCapacidadMaxima());
        vehiculosEntity.setUnidadesMedidaEntity(unidad);
        vehiculosEntity.setPlacas(dto.getPlacas());
        vehiculosEntity.setVigenciaPoliza(dto.getVigenciaPoliza());
        vehiculosEntity.setEstatusVehiculo(dto.getStatusVehiculo());
        vehiculosEntity.setDescripcion(dto.getDescripcion());
        vehiculosEntity.setDocumentoPoliza(dto.getDocumentoPoliza());
        vehiculosEntity.setNumeroPoliza(dto.getNumeroPoliza());
        vehiculosEntity.setEnabled(dto.isEnabled());
        return VehiculosValueMapper.VehiculosEntityToResponse(repository.save(vehiculosEntity));
    }

    @Override
    public VehiculosResponseDTO delete(UUID id) {
        VehiculosEntity vehiculosEntity = repository.findById(id).orElseThrow(()-> new IDNotFound("No se encontro el vehiculo"));
        vehiculosEntity.setDeleted(true);
        return VehiculosValueMapper.VehiculosEntityToResponse(repository.save(vehiculosEntity));
    }

    @Override
    public List<VehiculosResponseDTO> getAll(Map<String, String> params) {
        List<VehiculosEntity> list = repository.findAll(filterWithParameters(params));

        return list.stream().map(VehiculosValueMapper::VehiculosEntityToResponse).toList();
    }

    public Specification<VehiculosEntity> filterWithParameters(Map<String, String> params) {
        return new VehiculosSpecification().getSpecificationByFilters(params);
    }
}
