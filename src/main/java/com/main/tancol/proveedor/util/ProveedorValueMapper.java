package com.main.tancol.proveedor.util;


import com.main.tancol.proveedor.dto.request.ProveedorRequestDTO;
import com.main.tancol.proveedor.dto.request.TipoImpuestoRequestDTO;
import com.main.tancol.proveedor.dto.response.InformacionFiscalResponseDTO;
import com.main.tancol.proveedor.dto.response.ProveedorResponseDTO;
import com.main.tancol.proveedor.dto.response.ProveedorResponseDTO.TipoProveedorAsignado;
import com.main.tancol.proveedor.dto.response.TipoImpuestoResponseDTO;
import com.main.tancol.proveedor.entity.InformacionFiscal;
import com.main.tancol.proveedor.entity.TipoImpuestoEntity;
import com.main.tancol.proveedor.entity.ProveedorEntity;
import com.main.tancol.proveedor.entity.TipoProveedorEntity;

import java.util.Comparator;
import java.util.List;

public class ProveedorValueMapper {


    public static ProveedorEntity ProveedorRequestToEntity(ProveedorRequestDTO dto) {
        return ProveedorEntity.builder()
                .nombreComercial(dto.getNombreComercial())
                .correo(dto.getCorreo())
                .telefono(dto.getTelefono())
                .informacionFiscal((dto.getInformacionFiscal() == null) ? null : InformacionFiscalValueMapper.InformacionFiscalRequestToEntity(dto.getInformacionFiscal()))
                .condicionPago(dto.getCondicionPago())
                .build();
    }

    public static ProveedorResponseDTO ProveedorEntityToResponse(ProveedorEntity dto){
        List<TipoProveedorEntity> tipos = dto.getTiposProveedor() == null
                ? List.of()
                : dto.getTiposProveedor().stream()
                .sorted(Comparator.comparing(TipoProveedorEntity::getNombre, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .toList();
        TipoProveedorEntity primero = tipos.isEmpty() ? null : tipos.get(0);

        return  ProveedorResponseDTO.builder()
                .idProveedor(dto.getId())
                .nombreComercial(dto.getNombreComercial())
                .condicionPago(dto.getCondicionPago())
                .correo(dto.getCorreo())
                .telefono(dto.getTelefono())
                .nombreTipoProveedor(primero == null ? null : primero.getNombre())
                .idTipoProveedor(primero == null ? null : primero.getId())
                .tiposProveedor(tipos.stream().map(ProveedorValueMapper::tipoAsignado).toList())
                .infoFiscal((dto.getInformacionFiscal() == null) ? null : InformacionFiscalValueMapper.InformacionFiscalToResponseDTO(dto.getInformacionFiscal()))
                .enabled(dto.isEnabled())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .build();
    }

    private static TipoProveedorAsignado tipoAsignado(TipoProveedorEntity tipo) {
        return TipoProveedorAsignado.builder()
                .id(tipo.getId())
                .nombre(tipo.getNombre())
                .alias(tipo.getAlias())
                .build();
    }

    public static TipoImpuestoEntity TipoImpuestoRequestToEntity(TipoImpuestoRequestDTO dto){
        return TipoImpuestoEntity.builder()
                .nombre(dto.getNombre())
                .porcentaje(dto.getPorcentaje())
                .build();
    }

    public static TipoImpuestoResponseDTO TipoImpuestoEntityToResponse(TipoImpuestoEntity dto){
        return TipoImpuestoResponseDTO.builder()
                .id(dto.getId())
                .porcentaje(dto.getPorcentaje())
                .nombre(dto.getNombre())
                .enabled(dto.isEnabled())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .build();
    }


    public static InformacionFiscalResponseDTO InformacionFiscalToResponseDTO(InformacionFiscal dto){
        return InformacionFiscalResponseDTO.builder()
                .tipoVialidad(dto.getTipoVialidad())
                .tipoContribuyente(dto.getTipoContribuyenteFiscal())
                .rfc(dto.getRfc())
                .colonia(dto.getColonia())
                .nombreVialidad(dto.getNombreVialidad())
                .localidad(dto.getLocalidad())
                .municipio(dto.getMunicipio())
                .codigoPostal(dto.getCodigoPostal())
                .numeroInterior(dto.getNumeroInterior())
                .numeroExterior(dto.getNumeroExterior())
                .build();
    }

    }



