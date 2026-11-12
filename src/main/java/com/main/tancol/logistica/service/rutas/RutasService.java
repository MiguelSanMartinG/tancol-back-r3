package com.main.tancol.logistica.service.rutas;

import com.main.tancol.common.exeptions.IDNotFound;
import com.main.tancol.empleados.entities.EmpleadosEntity;
import com.main.tancol.empleados.repositories.EmpleadosRepository;
import com.main.tancol.logistica.dto.request.RutasRequestDTO;
import com.main.tancol.logistica.dto.response.RutasResponseDTO;
import com.main.tancol.logistica.entities.rutas.RutasEntity;
import com.main.tancol.logistica.entities.rutas.VehiculosEntity;
import com.main.tancol.logistica.repositories.RutasRepository;
import com.main.tancol.logistica.repositories.VehiculosRepository;
import com.main.tancol.logistica.service.interfaces.IRutasService;
import com.main.tancol.logistica.util.ruta.RutasSpecification;
import com.main.tancol.logistica.util.ruta.RutasValueMapper;
import com.main.tancol.producto.entities.comerciales.ProductoComercialEntity;
import com.main.tancol.producto.repositories.ProductoComercialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RutasService implements IRutasService {

    private final RutasRepository repository;
    private final EmpleadosRepository empleadosRepository;
    private final ProductoComercialRepository productoComercialRepository;
    private final VehiculosRepository vehiculosRepository;


    @Override
    @Transactional
    public RutasResponseDTO create(RutasRequestDTO dto) {

        EmpleadosEntity conductor = empleadosRepository.findById(dto.getIdChofer()).orElseThrow(() -> new IDNotFound("No se encontró el id conductor"));
        ProductoComercialEntity producto = productoComercialRepository.findById(dto.getIdProductoC()).orElseThrow(() -> new IDNotFound("No se encontro el id producto"));
        VehiculosEntity vehiculo = vehiculosRepository.findById(dto.getIdVehiculo()).orElseThrow(() -> new IDNotFound("No se encontró el vehiculo"));
        RutasEntity newRt = RutasValueMapper.RutasRequestToEntity(dto, conductor, producto,vehiculo);


        List<EmpleadosEntity> empleados = (List<EmpleadosEntity>) empleadosRepository.findAllById(dto.getEmpleadosAsignados());
        empleados.forEach(newRt::agregarEmpleado);


        return RutasValueMapper.RutasEntityToResponse(repository.save(newRt));
    }

    @Override
    public RutasResponseDTO getById(UUID id) {
        RutasEntity query = repository.findById(id).orElseThrow(()-> new IllegalArgumentException("Ruta no encontrada"));
        return RutasValueMapper.RutasEntityToResponse(query);
    }

    @Override
    @Transactional
    public RutasResponseDTO update(UUID id, RutasRequestDTO dto) {

        RutasEntity query = repository.findById(id).orElseThrow(()-> new IllegalArgumentException("Ruta no encontrada"));
        EmpleadosEntity conductor = empleadosRepository.findById(dto.getIdChofer()).orElseThrow(() -> new IDNotFound("No se encontró el id conductor"));
        ProductoComercialEntity producto = productoComercialRepository.findById(dto.getIdProductoC()).orElseThrow(() -> new IDNotFound("No se encontro el id producto"));
        VehiculosEntity vehiculo = vehiculosRepository.findById(dto.getIdVehiculo()).orElseThrow(() -> new IDNotFound("No se encontró el vehiculo"));;

        query.setNombre(dto.getNombre());
        query.setNumero(dto.getNumero());
        query.setChofer(conductor);
        query.setVehiculo(vehiculo);
        query.setProductBase(producto);
        query.setEnabled(dto.isEnabled());
        if (dto.getTipoRuta() != null) {
            query.setTipoRuta(dto.getTipoRuta());
        }

        List<EmpleadosEntity> empleados = (List<EmpleadosEntity>) empleadosRepository.findAllById(dto.getEmpleadosAsignados());
        query.actualizarLista(empleados);


        return RutasValueMapper.RutasEntityToResponse(repository.save(query));
    }

    @Override
    public RutasResponseDTO delete(UUID id) {
        RutasEntity query = repository.findById(id).orElseThrow(()-> new IllegalArgumentException("Ruta no encontrada"));
        query.setDeleted(true);
        return RutasValueMapper.RutasEntityToResponse(repository.save(query));
    }

    @Override
    public List<RutasResponseDTO> getAll(Map<String, String> params) {
        List<RutasEntity> list = repository.findAll(filterWithParameters(params));
        return list.stream().map(RutasValueMapper::RutasEntityToResponse).toList();
    }

    public Specification<RutasEntity> filterWithParameters(Map<String, String> params) {
        return new RutasSpecification().getSpecificationByFilters(params);
    }
}
