package com.main.tancol.orden.util;


import com.main.tancol.orden.dto.request.SolicitudOrdenRequestDTO;
import com.main.tancol.orden.dto.response.OrdenFacturacionResponseDTO;
import com.main.tancol.orden.dto.response.RecepcionOrdenResponseDTO;
import com.main.tancol.orden.dto.response.SolicitudOrdenResponseDTO;
import com.main.tancol.orden.entities.OrdenEntity;
import com.main.tancol.orden.entities.PartidaOrdenEntity;
import com.main.tancol.proveedor.entity.ProveedorEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OrdenValueMapper {

        public static OrdenEntity OrdenRequestDTOToEntity(SolicitudOrdenRequestDTO dto, String folio, ProveedorEntity proveedorEntity) {
        return OrdenEntity.builder()
                .fechaSolicitud(dto.getFechaSolicitud())
                .titulo(dto.getTitulo())
                .proveedorEntity(proveedorEntity)
                .folio(folio)
                .solicitanteEstatus(dto.getSolicitanteEstatus())
                .prioridad(dto.getPrioridad())
                .partidas(new ArrayList<>())
                .build();

    }

    public static SolicitudOrdenResponseDTO OrdenEntityToResponseDTO(OrdenEntity dto, List<PartidaOrdenEntity> listaDePartidasEntity){
        return  SolicitudOrdenResponseDTO.builder()
                .id(dto.getId())
                .folio(dto.getFolio())
                .fechaSolicitud(dto.getFechaSolicitud())
                .fechaUltimaModificacion((dto.getUpdatedAt() == null) ? dto.getCreatedAt() : dto.getUpdatedAt())
                .solicitanteEstatus(dto.getSolicitanteEstatus())
                .prioridad(dto.getPrioridad())
                .titulo(dto.getTitulo())
                .partidas(listaDePartidasEntity.stream().map(PartidaOrdenValueMapper::PartidasOrdenEntityToResponseDTO).collect(Collectors.toList()))
                .totalOrden(dto.getTotalOrden())
                .build();
    }


    public static OrdenFacturacionResponseDTO OrdenFacturacionEntityToResponseDTO(OrdenEntity dto){
        return OrdenFacturacionResponseDTO.builder()
                .id(dto.getId())
                .folioOrden(dto.getFolio())
                .proveedorId(dto.getProveedorEntity().getId())
                .proveedorNombre(dto.getProveedorEntity().getNombre())
                .proveedorRazonSocial(dto.getProveedorEntity().getRazonSocial())
                .estatusFactura(dto.getPostAutorizadoEstatus())
                .folioExterno(dto.getFolioExterno())
                .fechaFacturacion(dto.getFechaFacturacion())
                .updatedAt(dto.getUpdatedAt())
                .fechaSolicitud(dto.getFechaSolicitud())
                .totalOrden(dto.getTotalOrden())
                .comentarioFactura(dto.getComentarioFactura())
                .partidas(dto.getPartidas().stream().map(PartidaOrdenValueMapper::PartidasOrdenEntityToResponseDTO).collect(Collectors.toList()))
                .build();
    }

    public static RecepcionOrdenResponseDTO OrdenEntityToRecepcionOrdenResponseDTO(OrdenEntity dto){

            return RecepcionOrdenResponseDTO.builder()
                    .id(dto.getId())
                    .folio(dto.getFolio())
                    .fechaSolicitud(dto.getFechaSolicitud())
                    .recepcionEstatus(dto.getRecepcionEstatus())
                    .prioridad(dto.getPrioridad())
                    .fechaRecibo(dto.getFechaRecibo())
                    .usuario("Usuario 1")
                    .proveedor(com.main.tancol.proveedor.util.ValueMapper.ProveedorEntityToResponse(dto.getProveedorEntity()))
                    .recepcionEstatus(dto.getRecepcionEstatus())
                    .updatedAt(dto.getUpdatedAt())
                    .createdAt(dto.getCreatedAt())
                    .partidas(dto.getPartidas().stream().map(PartidaOrdenValueMapper::PartidasOrdenEntityToResponseDTO).collect(Collectors.toList()))
                    .build();

    }


}
