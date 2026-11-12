package com.main.tancol.cliente.service.interfaces;

import com.main.tancol.cliente.dto.request.ClienteProductoRequestDTO;
import com.main.tancol.cliente.dto.response.ClienteProductoResponseDTO;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface IClienteProductoService {

    ClienteProductoResponseDTO create(ClienteProductoRequestDTO dto);

    List<ClienteProductoResponseDTO> getAll(Map<String, String> params);

    ClienteProductoResponseDTO getById(UUID id);

    ClienteProductoResponseDTO update(UUID id , ClienteProductoRequestDTO dto);

    ClienteProductoResponseDTO delete(UUID dto);
}
