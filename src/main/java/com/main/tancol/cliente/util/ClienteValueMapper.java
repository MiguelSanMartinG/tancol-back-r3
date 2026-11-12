package com.main.tancol.cliente.util;


import com.main.tancol.cliente.dto.request.ClienteRequestDTO;
import com.main.tancol.cliente.dto.response.ClienteResponseBasicoDTO;
import com.main.tancol.cliente.dto.response.ClienteResponseDTO;
import com.main.tancol.cliente.entities.ClienteEntity;
import com.main.tancol.cliente.entities.TipoClienteEntity;
import com.main.tancol.logistica.entities.rutas.RutasEntity;
import com.main.tancol.logistica.util.ruta.RutasValueMapper;
import com.main.tancol.proveedor.enums.TipoPago;
import com.main.tancol.proveedor.util.InformacionFiscalValueMapper;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Collections;
import java.util.List;

public class ClienteValueMapper {




        public static ClienteEntity ClienteRequestToEntity(ClienteRequestDTO dto, TipoClienteEntity tipoClienteEntity, TipoPago tipoPago, RutasEntity rutasEntity) {
        return ClienteEntity.builder()
                .nombreComercial(dto.getNombreComercial())
                .tipoDeCliente(tipoClienteEntity)
                .rutaAsociada(rutasEntity)
//                .tipoContribuyente(dto.getTipoContribuyente())
                .diasDeCredito(dto.getDiasDeCredito())
                .tipoPago(tipoPago)
                .correo(dto.getCorreo())
                .telefono(dto.getTelefono())
                .fechaAlta(dto.getFechaAlta())
                .comentario(dto.getComentario())
                .informacionFiscal(ObjectUtils.isEmpty(dto.getInformacionFiscal()) ? null : InformacionFiscalValueMapper.InformacionFiscalRequestToEntity(dto.getInformacionFiscal()))
                .build();

    }

    public static ClienteResponseDTO ClienteEntityToResponse(ClienteEntity dto){
        return ClienteEntityToResponse(dto, Collections.emptyList(), Collections.emptyList());
    }

    public static ClienteResponseDTO ClienteEntityToResponse(ClienteEntity dto,
                                                            List<String> historialNombresComerciales,
                                                            List<String> historialRazonesSociales){
            if(dto == null)return null;
        return  ClienteResponseDTO.builder()
                .id(dto.getId())
                .noCliente(dto.getIdCliente())
                .correo(dto.getCorreo())
                .nombreComercial(dto.getNombreComercial())
                .tipoDeCliente(dto.getTipoDeCliente() != null ? TiposClientesValueMapper.TiposClientesEntityToSimpleResponse(dto.getTipoDeCliente()): null)
                .tipoPago(dto.getTipoPago())
                .diasDeCredito(dto.getDiasDeCredito())
                .telefono(dto.getTelefono())
                .fechaAlta(dto.getFechaAlta())
                .informacionFiscal(InformacionFiscalValueMapper.InformacionFiscalToResponseDTO(dto.getInformacionFiscal()))
                .rutaAsociada(ObjectUtils.isNotEmpty(dto.getRutaAsociada()) ? RutasValueMapper.RutasEntityToResponse(dto.getRutaAsociada()): null)
                .comentario(dto.getComentario())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .enabled(dto.isEnabled())
                .esEmpleado(dto.isEsEmpleado())
                .historialNombresComerciales(historialNombresComerciales != null ? historialNombresComerciales : Collections.emptyList())
                .historialRazonesSociales(historialRazonesSociales != null ? historialRazonesSociales : Collections.emptyList())
                .build();
    }

    public static ClienteResponseBasicoDTO ClienteEntityToResponseBasico(ClienteEntity dto){
        return  ClienteResponseBasicoDTO.builder()
                .idCliente(dto.getId())
                .nombreComercial(dto.getNombreComercial())
                .tipoDeCliente(dto.getTipoDeCliente() != null ? TiposClientesValueMapper.TiposClientesEntityToSimpleResponse(dto.getTipoDeCliente()): null)
                .enabled(dto.isEnabled())
                .esEmpleado(dto.isEsEmpleado())
                .build();
    }


}
