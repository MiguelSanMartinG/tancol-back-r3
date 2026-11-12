package com.main.tancol.almacen.service.interfaces;

import com.main.tancol.almacen.dto.request.InventarioRequestDTO;
import com.main.tancol.almacen.dto.response.InventarioResponseDTO;

import java.util.*;

public interface IInventarioService {

    InventarioResponseDTO create(InventarioRequestDTO dto);

    List<InventarioResponseDTO> getAll(Map<String, String> params);

    InventarioResponseDTO getById(UUID id);

    InventarioResponseDTO update(UUID id ,InventarioRequestDTO dto);

    InventarioResponseDTO delete(UUID dto);
}
