package com.main.tancol.liquidacion.util;


import com.main.tancol.empleados.entities.EmpleadosEntity;
import com.main.tancol.liquidacion.dto.request.LiquidacionRequestDTO;
import com.main.tancol.liquidacion.dto.response.LiquidacionListResponseDTO;
import com.main.tancol.liquidacion.dto.response.LiquidacionResponseDTO;
import com.main.tancol.liquidacion.entities.LiquidacionEntity;
import com.main.tancol.logistica.entities.rutas.RutasEntity;
import com.main.tancol.liquidacion.enums.EstadosLiquidacion;
import com.main.tancol.liquidacion.util.totalizados.LiquidacionTotalizadoValueMapper;
import com.main.tancol.logistica.util.ruta.RutasValueMapper;
import org.apache.commons.lang3.ObjectUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class LiquidacionValueMapper {

        public static LiquidacionEntity LiquidacionRequestToEntity(LiquidacionRequestDTO dto) {
        return LiquidacionEntity.builder()
                .fecha(dto.getFecha())
                .bloqueNota(dto.getBloqueNota())
                .estadosLiquidacion(ObjectUtils.isNotEmpty(dto.getEstado())? dto.getEstado(): EstadosLiquidacion.PENDIENTE)
                .observaciones(dto.getObservaciones())
                .liquidacionPartida(new LinkedList<>())
                .totalesPorProductos(new LinkedList<>())
                .build();

    }

    public static LiquidacionResponseDTO LiquidacionEntityToResponse(LiquidacionEntity dto){
        return  LiquidacionResponseDTO.builder()
                .idLiquidacion(dto.getIdLiquidacion())
                .folio(dto.getFolio())
                .ruta(ObjectUtils.isNotEmpty(dto.getRuta()) ? RutasValueMapper.RutasEntityToResponse(dto.getRuta()) : null)
                .bloqueNota(dto.getBloqueNota())
                .turno(dto.getTurno())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .estadosLiquidacion(dto.getEstadosLiquidacion())
                .origenBitacora(dto.isOrigenBitacora())
                .partidas(ObjectUtils.isNotEmpty(dto.getLiquidacionPartida()) ?
                        dto.getLiquidacionPartida().stream().map(LiquidacionPartidaValueMapper::LiquidacionPartidaEntityToResponse).collect(Collectors.toList())
                        : new ArrayList<>())
                .totalizados(ObjectUtils.isNotEmpty(dto.getTotalesPorProductos()) ?
                        dto.getTotalesPorProductos().stream().map(LiquidacionTotalizadoValueMapper::LiquidacionTotalizadoEntityToResponse).collect(Collectors.toList())
                        : new ArrayList<>())
                .observaciones(dto.getObservaciones())
                .fecha(dto.getFecha())
                .motivoCancelacion(dto.getMotivoCancelacion())
                .usuarioCancelacion(dto.getUsuarioCancelacion())
                .fechaCancelacion(dto.getFechaCancelacion())
                .build();
    }

    /** Fila ligera de la tabla; no toca partidas ni totalizados (vienen agregados por consulta). */
    public static LiquidacionListResponseDTO LiquidacionEntityToListResponse(LiquidacionEntity entity,
                                                                            long numeroPartidas,
                                                                            BigDecimal totalEstimado) {
        RutasEntity ruta = entity.getRuta();
        EmpleadosEntity chofer = ruta != null ? ruta.getChofer() : null;
        return new LiquidacionListResponseDTO(
                entity.getIdLiquidacion(),
                entity.getFolio(),
                entity.getFecha(),
                ruta != null ? new LiquidacionListResponseDTO.RutaResumen(ruta.getId(), ruta.getNombre(), ruta.getNumero()) : null,
                chofer != null ? new LiquidacionListResponseDTO.ChoferResumen(
                        chofer.getId(),
                        chofer.getNombre(),
                        chofer.getPrimerApellido(),
                        chofer.getSegundoApellido(),
                        chofer.getNombreUsuario()) : null,
                entity.getBloqueNota(),
                numeroPartidas,
                totalEstimado != null ? totalEstimado : BigDecimal.ZERO,
                entity.getEstadosLiquidacion(),
                entity.isOrigenBitacora(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }


}
