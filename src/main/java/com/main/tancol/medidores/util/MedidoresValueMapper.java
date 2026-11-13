package com.main.tancol.medidores.util;

import com.main.tancol.almacen.entities.UnidadesMedidaEntity;
import com.main.tancol.almacen.util.UnidadesMedidaValueMapper;
import com.main.tancol.medidores.dto.request.MedidoresRequestDTO;
import com.main.tancol.medidores.dto.response.MedidoresResponseDTO;
import com.main.tancol.medidores.dto.response.MedidoresResponseSimpleDTO;
import com.main.tancol.medidores.dto.response.SistemasDeAlmacenamientoResponseDTO;
import com.main.tancol.medidores.entities.MedidoresEntity;
import com.main.tancol.medidores.entities.SistemasDeAlmacenamientoEntity;
import org.apache.commons.lang3.ObjectUtils;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class MedidoresValueMapper {

        public static MedidoresEntity MedidoresRequestToEntity(MedidoresRequestDTO dto, UnidadesMedidaEntity unidadesMedida,
                                                               Set<SistemasDeAlmacenamientoEntity> sistemasDeAlmacenamiento) {
        LinkedHashSet<SistemasDeAlmacenamientoEntity> sistemas = sistemasDeAlmacenamiento == null
                ? new LinkedHashSet<>()
                : new LinkedHashSet<>(sistemasDeAlmacenamiento);
        return MedidoresEntity.builder()
                .nombre(dto.getNombre())
                .alias(dto.getAlias())
                .tipoAgua(dto.getTipoAgua())
                .medicion(dto.getMedicionInicial())
                .alertaConsumoLitros(dto.getAlertaConsumoLitros())
                .sistemasDeAlmacenamiento(sistemas)
                .unidadadMedidaDefault(unidadesMedida)
                .build();

    }

    private static List<SistemasDeAlmacenamientoResponseDTO> sistemasDeAlmacenamientoToResponseList(MedidoresEntity dto) {
        if (dto.getSistemasDeAlmacenamiento() == null || dto.getSistemasDeAlmacenamiento().isEmpty()) {
            return List.of();
        }
        return dto.getSistemasDeAlmacenamiento().stream()
                .map(SistemasDeAlmacenamientoValueMapper::SistemasDeAlmacenamientoEntityToResponse)
                .toList();
    }

    public static MedidoresResponseDTO MedidoresEntityToResponse(MedidoresEntity dto) {
        return MedidoresEntityToResponse(dto, MedidorEstadoContext.VACIO);
    }

    public static MedidoresResponseDTO MedidoresEntityToResponse(MedidoresEntity dto, MedidorEstadoContext estado) {
        boolean bitacoraHoy = estado.tieneBitacoraHoy(dto.getIdMedidor());
        BigDecimal consumoHoy = estado.getConsumoHoy(dto.getIdMedidor());
        ResumenPadre resumenPadre = calcularResumenPadre(dto, estado);

        return MedidoresResponseDTO.builder()
                .idMedidor(dto.getIdMedidor())
                .nombre(dto.getNombre())
                .alias(dto.getAlias())
                .tipoAgua(dto.getTipoAgua())
                .enabled(dto.isEnabled())
                .bitacoraHoy(bitacoraHoy)
                .medidoHoy(bitacoraHoy)
                .consumoHoy(consumoHoy)
                .sumaConsumoHijos(resumenPadre != null ? resumenPadre.sumaConsumoHijos() : null)
                .diferenciaVsHijos(resumenPadre != null ? resumenPadre.diferenciaVsHijos() : null)
                .todosHijosMedidosHoy(resumenPadre != null ? resumenPadre.todosHijosMedidosHoy() : null)
                .sistemasDeAlmacenamiento(sistemasDeAlmacenamientoToResponseList(dto))
                .unidadMedida(ObjectUtils.isNotEmpty(dto.getUnidadadMedidaDefault()) ? UnidadesMedidaValueMapper.UnidadesMedidaEntityToResponse(dto.getUnidadadMedidaDefault()) : null)
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .deleted(dto.isDeleted())
                .ultimaMedicion(dto.getMedicion())
                .alertaConsumoLitros(dto.getAlertaConsumoLitros())
                .medidoresHijos(ObjectUtils.isNotEmpty(dto.getMedidoresHijos())
                        ? dto.getMedidoresHijos().stream().map(h -> MedidoresEntityToResponseSimple(h, estado)).toList()
                        : null)
                .fechaUltimaMedicion(dto.getFechaUltimaMedicion())
                .build();
    }

    public static MedidoresResponseSimpleDTO MedidoresEntityToResponseSimple(MedidoresEntity dto) {
        return MedidoresEntityToResponseSimple(dto, MedidorEstadoContext.VACIO);
    }

    public static MedidoresResponseSimpleDTO MedidoresEntityToResponseSimple(MedidoresEntity dto, MedidorEstadoContext estado) {
        boolean bitacoraHoy = estado.tieneBitacoraHoy(dto.getIdMedidor());
        return MedidoresResponseSimpleDTO.builder()
                .idMedidor(dto.getIdMedidor())
                .nombre(dto.getNombre())
                .alias(dto.getAlias())
                .tipoAgua(dto.getTipoAgua())
                .enabled(dto.isEnabled())
                .bitacoraHoy(bitacoraHoy)
                .medidoHoy(bitacoraHoy)
                .consumoHoy(estado.getConsumoHoy(dto.getIdMedidor()))
                .sistemasDeAlmacenamiento(sistemasDeAlmacenamientoToResponseList(dto))
                .unidadMedida(ObjectUtils.isNotEmpty(dto.getUnidadadMedidaDefault()) ? UnidadesMedidaValueMapper.UnidadesMedidaEntityToResponse(dto.getUnidadadMedidaDefault()) : null)
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .deleted(dto.isDeleted())
                .ultimaMedicion(dto.getMedicion())
                .fechaUltimaMedicion(dto.getFechaUltimaMedicion())
                .build();
    }

    private static ResumenPadre calcularResumenPadre(MedidoresEntity padre, MedidorEstadoContext estado) {
        if (padre.getMedidoresHijos() == null || padre.getMedidoresHijos().isEmpty()) {
            return null;
        }

        BigDecimal sumaConsumoHijos = BigDecimal.ZERO;
        boolean todosHijosMedidosHoy = true;

        for (MedidoresEntity hijo : padre.getMedidoresHijos()) {
            if (!estado.tieneBitacoraHoy(hijo.getIdMedidor())) {
                todosHijosMedidosHoy = false;
                continue;
            }
            BigDecimal consumoHijo = estado.getConsumoHoy(hijo.getIdMedidor());
            if (consumoHijo != null) {
                sumaConsumoHijos = sumaConsumoHijos.add(consumoHijo);
            }
        }

        BigDecimal consumoPadre = estado.getConsumoHoy(padre.getIdMedidor());
        BigDecimal diferenciaVsHijos = consumoPadre != null
                ? consumoPadre.subtract(sumaConsumoHijos)
                : null;

        return new ResumenPadre(sumaConsumoHijos, diferenciaVsHijos, todosHijosMedidosHoy);
    }

    private record ResumenPadre(
            BigDecimal sumaConsumoHijos,
            BigDecimal diferenciaVsHijos,
            boolean todosHijosMedidosHoy
    ) {}
}
