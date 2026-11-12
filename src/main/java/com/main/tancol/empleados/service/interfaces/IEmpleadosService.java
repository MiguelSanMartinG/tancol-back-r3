package com.main.tancol.empleados.service.interfaces;

import com.main.tancol.common.dto.PageResponse;
import com.main.tancol.empleados.dto.request.EmpleadosRequestDTO;
import com.main.tancol.empleados.dto.response.EmpleadosResponseDTO;

import java.util.*;

public interface IEmpleadosService {

    EmpleadosResponseDTO create(EmpleadosRequestDTO dto);

    List<EmpleadosResponseDTO> getAll(Map<String, String> params);

    PageResponse<EmpleadosResponseDTO> getPage(Map<String, String> params);

    EmpleadosResponseDTO getById(UUID id);

    EmpleadosResponseDTO update(UUID id ,EmpleadosRequestDTO dto);

    EmpleadosResponseDTO delete(UUID dto);
}
