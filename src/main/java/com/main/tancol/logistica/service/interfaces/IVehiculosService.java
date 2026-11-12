package com.main.tancol.logistica.service.interfaces;

import com.main.tancol.logistica.dto.request.VehiculosRequestDTO;
import com.main.tancol.logistica.dto.response.VehiculosResponseDTO;

import java.util.*;

public interface IVehiculosService {

    VehiculosResponseDTO create(VehiculosRequestDTO dto);

    List<VehiculosResponseDTO> getAll(Map<String, String> params);

    VehiculosResponseDTO getById(UUID id);

    VehiculosResponseDTO update(UUID id ,VehiculosRequestDTO dto);

    VehiculosResponseDTO delete(UUID dto);
}
