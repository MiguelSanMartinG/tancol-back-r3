package com.main.tancol.orden.util;


import com.main.tancol.orden.dto.request.SolicitudOrdenRequestDTO;
import com.main.tancol.orden.dto.response.FacturacionOrdenResponseDTO;
import com.main.tancol.orden.dto.response.RecepcionOrdenResponseDTO;
import com.main.tancol.orden.dto.response.SolicitudOrdenResponseDTO;
import com.main.tancol.orden.entities.OrdenEntity;
import com.main.tancol.orden.entities.PartidaOrdenEntity;
import com.main.tancol.proveedor.entity.ProveedorEntity;
import com.main.tancol.proveedor.util.ProveedorValueMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class OrdenValueMapper {

    public static OrdenEntity SolicitudRequestDTOToEntity(SolicitudOrdenRequestDTO dto, String folio, ProveedorEntity proveedorEntity) {
        return OrdenEntity.builder()
                .titulo(Optional.ofNullable(dto.getTitulo()).orElse(null))
                .folio(Optional.ofNullable(folio).orElse(null))
                .prioridad(dto.getPrioridad())
                .proveedor(Optional.ofNullable(proveedorEntity).orElse(null))
                .solicitudEstatus(dto.getSolicitudEstatus())
                .partidas(new ArrayList<>())
                .build();
    }

    public static SolicitudOrdenResponseDTO OrdenEntityToResponseDTO(OrdenEntity dto, List<PartidaOrdenEntity> listaDePartidasEntity){

        return  SolicitudOrdenResponseDTO.builder()
                .idSolicitud(dto.getId())
                .titulo(dto.getTitulo())
                .folio(dto.getFolio())
                .totalOrden(dto.getTotalOrden())
                .fechaCreacionSolicitud(dto.getGetFechaCreacionSolicitud())
                .fechaModificacionSolicitud((dto.getUpdatedAt() == null) ? dto.getCreatedAt() : dto.getUpdatedAt())
                .solicitudEstatus(dto.getSolicitudEstatus())
                .prioridad(dto.getPrioridad())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .proveedor(dto.getProveedor() == null ? null : ProveedorValueMapper.ProveedorEntityToResponse(dto.getProveedor()))
                .partidas(listaDePartidasEntity.isEmpty()? new ArrayList<>() :
                        listaDePartidasEntity.stream().map(PartidaOrdenValueMapper::PartidasOrdenEntityToResponseDTO).collect(Collectors.toList()))
                .build();
    }


    public static FacturacionOrdenResponseDTO OrdenFacturacionEntityToResponseDTO(OrdenEntity dto){
        return  FacturacionOrdenResponseDTO.builder()
                .idSolicitud(dto.getId())
                .titulo(dto.getTitulo())
                .folio(dto.getFolio())
                .totalOrden(dto.getTotalOrden())
                .fechaCreacionSolicitud(dto.getGetFechaCreacionSolicitud())
                .fechaModificacionSolicitud((dto.getUpdatedAt() == null) ? dto.getCreatedAt() : dto.getUpdatedAt())
                .solicitudEstatus(dto.getSolicitudEstatus())
                .prioridad(dto.getPrioridad())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .proveedor(ProveedorValueMapper.ProveedorEntityToResponse(dto.getProveedor()))
                .partidas(dto.getPartidas().stream().map(PartidaOrdenValueMapper::PartidasOrdenEntityToResponseDTO).collect(Collectors.toList()))
                .fechaFacturacion(dto.getFechaFacturacion())
                .folioExterno(dto.getFolioExterno())
                .estatusFactura(dto.getFacturacionEstado())
                .comentarioFactura(dto.getComentarioFactura())
                .build();
    }

    public static RecepcionOrdenResponseDTO OrdenEntityToRecepcionOrdenResponseDTO(OrdenEntity dto){

            return RecepcionOrdenResponseDTO.builder()
                    .fechaRecibo(dto.getFechaRecibo())
//                    .comentarioRecibo(dto.getComentarioRecibo())
                    .recepcionEstatus(dto.getRecepcionEstatus())
                    .usuario(dto.getUsuario())
                    .idSolicitud(dto.getId())
                    .titulo(dto.getTitulo())
                    .folio(dto.getFolio())
                    .totalOrden(dto.getTotalOrden())
                    .fechaCreacionSolicitud(dto.getGetFechaCreacionSolicitud())
                    .fechaModificacionSolicitud((dto.getUpdatedAt() == null) ? dto.getCreatedAt() : dto.getUpdatedAt())
                    .solicitudEstatus(dto.getSolicitudEstatus())
                    .prioridad(dto.getPrioridad())
                    .createdAt(dto.getCreatedAt())
                    .updatedAt(dto.getUpdatedAt())
                    .proveedor(ProveedorValueMapper.ProveedorEntityToResponse(dto.getProveedor()))
                    .partidas(dto.getPartidas().stream().map(PartidaOrdenValueMapper::PartidasOrdenEntityToResponseDTO).collect(Collectors.toList()))
                    .fechaFacturacion(dto.getFechaFacturacion())
                    .folioExterno(dto.getFolioExterno())
                    .estatusFactura(dto.getFacturacionEstado())
                    .comentarioFactura(dto.getComentarioFactura())
                    .build();

    }



}
