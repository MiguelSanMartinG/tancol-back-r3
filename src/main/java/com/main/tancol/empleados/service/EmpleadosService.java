package com.main.tancol.empleados.service;

import com.main.tancol.cliente.service.interfaces.IClienteService;
import com.main.tancol.common.exeptions.IDNotFound;
import com.main.tancol.empleados.dto.request.EmpleadosRequestDTO;
import com.main.tancol.empleados.dto.response.EmpleadosResponseDTO;
import com.main.tancol.empleados.entities.DepartamentoEntity;
import com.main.tancol.empleados.entities.EmpleadosEntity;
import com.main.tancol.empleados.entities.TiposEmpleadosEntity;
import com.main.tancol.empleados.repositories.DepartamentoRepository;
import com.main.tancol.empleados.repositories.EmpleadosRepository;
import com.main.tancol.empleados.repositories.TiposEmpleadosRepository;
import com.main.tancol.empleados.service.interfaces.IEmpleadosService;
import com.main.tancol.empleados.util.EmpleadosSpecification;
import com.main.tancol.empleados.util.EmpleadosValueMapper;
import com.main.tancol.permisos.entities.RoleEntity;
import com.main.tancol.permisos.repositories.RoleRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmpleadosService implements IEmpleadosService {

    private final EmpleadosRepository repository;
    private final DepartamentoRepository departamentoRepository;
    private final TiposEmpleadosRepository tiposEmpleadosRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final IClienteService clienteService;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public EmpleadosResponseDTO create(EmpleadosRequestDTO dto) {
        DepartamentoEntity query = departamentoRepository.findById(dto.getIdDepartamento()).orElseThrow(() -> new IllegalArgumentException("Departamento no encontrado"));
        TiposEmpleadosEntity query2 = tiposEmpleadosRepository.findById(dto.getIdTipoEmpleado()).orElseThrow(() -> new IllegalArgumentException("Tipo empleado no encontrado"));
        EmpleadosEntity entity =  EmpleadosValueMapper.EmpleadosRequestToEntity(dto,query2, query);
        entity.setIdEmpleado(Integer.toUnsignedLong(getNextIdExterno()));
        entity.setContra(passwordEncoder.encode(dto.getPassword()));
        entity.setEnabled(true);

        RoleEntity rol;
        if(dto.getIdRol() != null){
            rol = roleRepository.findById(dto.getIdRol()).orElseThrow(() -> new IllegalArgumentException("Rol no encontrado"));
            entity.setRole(rol);
        }

        EmpleadosEntity saved = repository.save(entity);
        clienteService.crearDesdeEmpleado(saved);
        return EmpleadosValueMapper.EmpleadosEntityToResponse(saved);
    }

    @Override
    public EmpleadosResponseDTO getById(UUID id) {
        EmpleadosEntity query = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Empleada no encontrada"));
        return EmpleadosValueMapper.EmpleadosEntityToResponse(query);
    }

    @Override
    @Transactional
    public EmpleadosResponseDTO update(UUID id, EmpleadosRequestDTO dto) {

        EmpleadosEntity oldEmpleado = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Empleada no encontrada"));

        if(oldEmpleado.getIdEmpleado() == 0) {
            throw new IDNotFound("No es posible modificar al Administrador");
        }

        TiposEmpleadosEntity TEentity = tiposEmpleadosRepository.findById(dto.getIdTipoEmpleado()).orElseThrow(() -> new IllegalArgumentException("Tipo empleado no encontrada"));
        DepartamentoEntity DEentity = departamentoRepository.findById(dto.getIdDepartamento()).orElseThrow(() -> new IllegalArgumentException("Departamento no encontrada"));
        RoleEntity rol = null;
        if (dto.getIdRol() != null){
           rol =  roleRepository.findById(dto.getIdRol()).orElseThrow(() -> new IllegalArgumentException("Rol no encontrado"));
        }

        oldEmpleado.setNombre(dto.getNombre());
        oldEmpleado.setPrimerApellido(dto.getApellidoPaterno());
        oldEmpleado.setSegundoApellido(dto.getApellidoMaterno());
        oldEmpleado.setFechaNacimiento(dto.getFechaNacimiento());
        oldEmpleado.setTelefono(dto.getTelefono());
        oldEmpleado.setDepartamento(DEentity);
        oldEmpleado.setTiposEmpleados(TEentity);
        oldEmpleado.setNombreUsuario(dto.getCorreo());
        oldEmpleado.setEnabled(dto.isEnabled());
        oldEmpleado.setRole(rol);
        oldEmpleado.setCorreo(dto.getCorreo());
        if(dto.getPassword() != null){
        oldEmpleado.setContra(passwordEncoder.encode(dto.getPassword()));
        }
        return EmpleadosValueMapper.EmpleadosEntityToResponse(repository.save(oldEmpleado));
    }

    @Override
    public EmpleadosResponseDTO delete(UUID id) {
        EmpleadosEntity oldEmpleado = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Empleado no encontrada"));
        if(oldEmpleado.getIdEmpleado() == 0) {
            throw new IDNotFound("No se puede modificar al Administrador");
        }

        oldEmpleado.setDeleted(true);
        this.repository.save(oldEmpleado);
        return EmpleadosValueMapper.EmpleadosEntityToResponse(oldEmpleado);
    }

    @Override
    public List<EmpleadosResponseDTO> getAll(Map<String, String> params) {
        List<EmpleadosEntity> list = repository.findAll(filterWithParameters(params));
        return list.stream().map(EmpleadosValueMapper::EmpleadosEntityToResponse).toList();
    }

    public Specification<EmpleadosEntity> filterWithParameters(Map<String, String> params) {
        return new EmpleadosSpecification().getSpecificationByFilters(params);
    }

    public Integer getNextIdExterno() {

            Integer numero =  ((Number) entityManager
                    .createNativeQuery("SELECT nextval('empleado_id_externo_seq')")
                    .getSingleResult()).intValue();
            return numero;

    }
}
