package com.main.tancol.almacen.util;


import com.main.tancol.almacen.dto.request.TransferenciasRequestDTO;
import com.main.tancol.almacen.dto.response.TransferenciasResponseDTO;
import com.main.tancol.almacen.entities.TransferenciasEntity;
import com.main.tancol.empleados.util.EmpleadosValueMapper;

public class TransferenciasValueMapper {

        public static TransferenciasEntity TransferenciasRequestToEntity(TransferenciasRequestDTO dto) {
        return TransferenciasEntity.builder()
                .build();

    }

    public static TransferenciasResponseDTO TransferenciasEntityToResponse(TransferenciasEntity dto) {
        return  TransferenciasResponseDTO.builder()
                .fechaTransferencia(dto.getFechaTransferencia())
                .idTransferencia(dto.getIdTransferencia())
                .motivo(dto.getMotivo())
                .comentario(dto.getComentarios())
                .idEmpleado(EmpleadosValueMapper.EmpleadosEntityToSimpleResponse(dto.getEmpleado()))
                .transferencias(dto.getPartidas().stream().map(PartidaTransferenciaValueMapper::PartidaTransferenciaEntityToResponse).toList())
                .build();
    }


}
