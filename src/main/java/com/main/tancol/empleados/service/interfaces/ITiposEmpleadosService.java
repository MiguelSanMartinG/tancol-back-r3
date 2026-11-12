package com.main.tancol.empleados.service.interfaces;

import com.main.tancol.empleados.dto.request.TiposEmpleadosRequestDTO;
import com.main.tancol.empleados.dto.response.TiposEmpleadosResponseDTO;

import java.util.*;

public interface ITiposEmpleadosService {

    TiposEmpleadosResponseDTO create(TiposEmpleadosRequestDTO dto);

    List<TiposEmpleadosResponseDTO> getAll(Map<String, String> params);

    TiposEmpleadosResponseDTO getById(UUID id);

    TiposEmpleadosResponseDTO update(UUID id ,TiposEmpleadosRequestDTO dto);

    TiposEmpleadosResponseDTO delete(UUID dto);
}
