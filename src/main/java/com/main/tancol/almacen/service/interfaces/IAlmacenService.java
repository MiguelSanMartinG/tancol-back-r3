package com.main.tancol.almacen.service.interfaces;

import com.main.tancol.almacen.dto.request.AlmacenRequestDTO;
import com.main.tancol.almacen.dto.response.AlmacenResponseDTO;
import com.main.tancol.common.dto.PageResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface IAlmacenService {
    AlmacenResponseDTO createAlmacen(AlmacenRequestDTO dto);

    AlmacenResponseDTO readByID(UUID id);

    AlmacenResponseDTO updateAlmacen(UUID id, AlmacenRequestDTO dto);

    AlmacenResponseDTO deleteAlmacen(UUID id);

    List<AlmacenResponseDTO> getAll(Map<String, String> params);

    PageResponse<AlmacenResponseDTO> getPage(Map<String, String> params);

}
