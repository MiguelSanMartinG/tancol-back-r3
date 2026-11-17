package com.main.tancol.almacen.service.interfaces;

import com.main.tancol.almacen.dto.request.TransferenciasRequestDTO;
import com.main.tancol.almacen.dto.response.TransferenciasResponseDTO;
import com.main.tancol.common.dto.PageResponse;

import java.util.*;

public interface ITransferenciasService {

    TransferenciasResponseDTO create(TransferenciasRequestDTO dto);

    List<TransferenciasResponseDTO> getAll(Map<String, String> params);

    PageResponse<TransferenciasResponseDTO> getPage(Map<String, String> params);

    TransferenciasResponseDTO getById(UUID id);

    TransferenciasResponseDTO update(UUID id ,TransferenciasRequestDTO dto);

    TransferenciasResponseDTO delete(UUID dto);
}
