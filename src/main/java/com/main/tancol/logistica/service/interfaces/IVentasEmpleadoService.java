package com.main.tancol.logistica.service.interfaces;

import com.main.tancol.logistica.dto.request.partidas.VentasEmpleadoRequestDTO;
import com.main.tancol.logistica.dto.response.VentasEmpleadoResponseDTO;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface IVentasEmpleadoService {

    VentasEmpleadoResponseDTO create(VentasEmpleadoRequestDTO dto);

    List<VentasEmpleadoResponseDTO> getAll(Map<String, String> params);

    VentasEmpleadoResponseDTO getById(UUID id);

    VentasEmpleadoResponseDTO update(UUID id ,VentasEmpleadoRequestDTO dto);

    VentasEmpleadoResponseDTO delete(UUID dto);
}
