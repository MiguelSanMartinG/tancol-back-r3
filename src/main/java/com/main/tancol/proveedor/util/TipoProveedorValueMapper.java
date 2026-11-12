package com.main.tancol.proveedor.util;


import com.main.tancol.proveedor.entity.TipoProveedorEntity;
import com.main.tancol.proveedor.dto.request.*;
import com.main.tancol.proveedor.dto.response.*;
import com.main.tancol.proveedor.entity.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class TipoProveedorValueMapper {

    public static TipoProveedorResponseDTO TipoProveedorEntityToResponse(TipoProveedorEntity dto){
        return  TipoProveedorResponseDTO.builder()
                .id(dto.getId())
                .nombre(dto.getNombre())
                .alias(dto.getAlias())
                .updatedAt(dto.getUpdatedAt())
                .createdAt(dto.getCreatedAt())
                .enabled(dto.isEnabled())
                .proveedores(List.of())
                .build();
    }

    public static InformacionFiscalResponseDTO InformacionFiscalToResponseDTO(InformacionFiscal dto) {
        return InformacionFiscalResponseDTO.builder()
                .codigoPostal(Optional.ofNullable(dto.getCodigoPostal()).orElse(null))
                .numeroExterior(Optional.ofNullable(dto.getNumeroExterior()).orElse(null))
                .numeroInterior(Optional.ofNullable(dto.getNumeroInterior()).orElse(null))
                .tipoContribuyente(Optional.ofNullable(dto.getTipoContribuyenteFiscal()).orElse(null))
                .tipoVialidad(Optional.ofNullable(dto.getTipoVialidad()).orElse(null))
                .colonia(Optional.ofNullable(dto.getColonia()).orElse(null))
                .localidad(Optional.ofNullable(dto.getLocalidad()).orElse(null))
                .municipio(Optional.ofNullable(dto.getMunicipio()).orElse(null))
                .nombreVialidad(Optional.ofNullable(dto.getNombreVialidad()).orElse(null))
                .rfc(Optional.ofNullable(dto.getRfc()).orElse(null))
                .build();

    }


}
