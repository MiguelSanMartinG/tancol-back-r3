package com.main.tancol.logistica.service.bitacoras;

import com.main.tancol.common.exeptions.IDNotFound;
import com.main.tancol.empleados.entities.EmpleadosEntity;
import com.main.tancol.empleados.repositories.EmpleadosRepository;
import com.main.tancol.logistica.dto.request.partidas.VentasEmpleadoRequestDTO;
import com.main.tancol.logistica.dto.response.VentasEmpleadoResponseDTO;
import com.main.tancol.logistica.entities.bitacora.VentaTrayectoEntity;
import com.main.tancol.logistica.entities.bitacora.comisiones.VentasEmpleadoEntity;
import com.main.tancol.logistica.repositories.VentasEmpleadoRepository;
import com.main.tancol.logistica.service.interfaces.IVentasEmpleadoService;
import com.main.tancol.logistica.util.VentasEmpleadoSpecification;
import com.main.tancol.logistica.util.VentasEmpleadoValueMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class VentasEmpleadoService implements IVentasEmpleadoService {

    private final VentasEmpleadoRepository repository;
    private final VentasEmpleadoRepository ventasEmpleadoRepository;
    private final EmpleadosRepository empleadosRepository;


    @Override
    public VentasEmpleadoResponseDTO create(VentasEmpleadoRequestDTO dto) {


        return null;
    }

    @Override
    public VentasEmpleadoResponseDTO getById(UUID id) {
        return null;
    }

    @Override
    public VentasEmpleadoResponseDTO update(UUID id, VentasEmpleadoRequestDTO dto) {
        return null;
    }

    @Override
    public VentasEmpleadoResponseDTO delete(UUID id) {
        return null;
    }

    @Override
    public List<VentasEmpleadoResponseDTO> getAll(Map<String, String> params) {
        List<VentasEmpleadoEntity> list = repository.findAll(filterWithParameters(params));
        return list.stream().map(VentasEmpleadoValueMapper::VentasEmpleadoEntityToResponse).toList();
    }

    public Specification<VentasEmpleadoEntity> filterWithParameters(Map<String, String> params) {
        return new VentasEmpleadoSpecification().getSpecificationByFilters(params);
    }

    public VentaTrayectoEntity create(VentaTrayectoEntity p, Set<VentasEmpleadoRequestDTO> comisionesEmpleado) {

        if(comisionesEmpleado.isEmpty()) {
            return p;
        }

        for (VentasEmpleadoRequestDTO dto : comisionesEmpleado) {

//            log.info(String.valueOf(dto.getCantidad()));
            EmpleadosEntity ayudante = null;
            if(dto.getIdEmpleado() != null) {
                ayudante = empleadosRepository.findById(dto.getIdEmpleado())
                        .orElseThrow(() -> new IDNotFound("No se encontró al empleado"));
            }
            VentasEmpleadoEntity v = VentasEmpleadoEntity.builder()
                    .empleado(ayudante)
                    .comision(dto.getCantidad())
                    .build();

            p.agregarComision(v);
        }
        return p;
    }
}
