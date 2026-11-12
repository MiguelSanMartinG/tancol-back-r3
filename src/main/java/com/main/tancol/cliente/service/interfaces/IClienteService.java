package com.main.tancol.cliente.service.interfaces;

import com.main.tancol.cliente.dto.request.ClienteRequestDTO;
import com.main.tancol.cliente.dto.response.ClienteNombreHistorialResponseDTO;
import com.main.tancol.cliente.dto.response.ClienteResponseDTO;
import com.main.tancol.empleados.entities.EmpleadosEntity;

import java.util.*;

public interface IClienteService {

    ClienteResponseDTO create(ClienteRequestDTO dto);

    void crearDesdeEmpleado(EmpleadosEntity empleado);

    List<ClienteResponseDTO> getAll(Map<String, String> params);

    ClienteResponseDTO getById(UUID id);

    ClienteResponseDTO update(UUID id ,ClienteRequestDTO dto);

    ClienteResponseDTO delete(UUID dto);

    List<ClienteNombreHistorialResponseDTO> getHistorialNombreComercial(UUID id);

    List<ClienteNombreHistorialResponseDTO> getHistorialRazonSocial(UUID id);
}
