package com.main.tancol.medidores.service.interfaces;

import com.main.tancol.medidores.dto.request.MedidoresRequestDTO;
import com.main.tancol.medidores.dto.response.MedidoresResponseDTO;

import java.util.*;

public interface IMedidoresService {

    MedidoresResponseDTO create(MedidoresRequestDTO dto);

    List<MedidoresResponseDTO> getAll(Map<String, String> params);

    MedidoresResponseDTO getById(UUID id);

    MedidoresResponseDTO update(UUID id ,MedidoresRequestDTO dto);

    MedidoresResponseDTO delete(UUID dto);
}
