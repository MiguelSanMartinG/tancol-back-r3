package com.main.tancol.proveedor.util;

import com.main.tancol.proveedor.dto.request.InformacionFiscalRequestDTO;
import com.main.tancol.proveedor.dto.response.InformacionFiscalResponseDTO;
import com.main.tancol.proveedor.entity.InformacionFiscal;

import java.util.Optional;

public class InformacionFiscalValueMapper {


        public static InformacionFiscalResponseDTO InformacionFiscalToResponseDTO(InformacionFiscal dto) {
            if(dto == null) return null;
            return InformacionFiscalResponseDTO.builder()
                    .tipoContribuyente(Optional.ofNullable(dto.getTipoContribuyenteFiscal()).orElse(null))
                    .razonSocial(Optional.ofNullable(dto.getRazonSocial()).orElse(null))
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
                    .entidadFederativa(Optional.ofNullable(dto.getEntidadFederativa()).orElse(null))
                    .calle1(Optional.ofNullable(dto.getCalle1()).orElse(null))
                    .calle2(Optional.ofNullable(dto.getCalle2()).orElse(null))
                    .build();

        }

    public static InformacionFiscal InformacionFiscalRequestToEntity(InformacionFiscalRequestDTO dto){
        return InformacionFiscal.builder()
                .tipoVialidad(Optional.ofNullable(dto.getTipoVialidad()).orElse(null))
                .razonSocial(Optional.ofNullable(dto.getRazonSocial()).orElse(null))
                .tipoContribuyenteFiscal(Optional.ofNullable(dto.getTipoContribuyente()).orElse(null))
                .rfc(Optional.ofNullable(dto.getRfc()).orElse(null))
                .codigoPostal(Optional.ofNullable(dto.getCodigoPostal()).orElse(null))
                .nombreVialidad(Optional.ofNullable(dto.getNombreVialidad()).orElse(null))
                .numeroInterior(Optional.ofNullable(dto.getNumeroInterior()).orElse(null))
                .numeroExterior(Optional.ofNullable(dto.getNumeroExterior()).orElse(null))
                .municipio(Optional.ofNullable(dto.getMunicipio()).orElse(null))
                .localidad(Optional.ofNullable(dto.getLocalidad()).orElse(null))
                .colonia(Optional.ofNullable(dto.getColonia()).orElse(null))
                .entidadFederativa(Optional.ofNullable(dto.getEntidadFederativa()).orElse(null))
                .calle1(Optional.ofNullable(dto.getCalle1()).orElse(null))
                .calle2(Optional.ofNullable(dto.getCalle2()).orElse(null))
                .build();
    }
}
