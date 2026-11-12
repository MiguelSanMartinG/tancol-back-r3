package com.main.tancol.dashboard.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DevolucionesEquiposResponseDTO {
    private int totalDevoluciones;
    private int equiposDevueltos;
    private int clientesConDevoluciones;
    @Builder.Default
    private List<TipoEquipoCantidadResponseDTO> porTipoEquipo = new ArrayList<>();
    @Builder.Default
    private List<CantidadPorDiaResponseDTO> porDia = new ArrayList<>();

    public static DevolucionesEquiposResponseDTO vacio() {
        return DevolucionesEquiposResponseDTO.builder()
                .totalDevoluciones(0)
                .equiposDevueltos(0)
                .clientesConDevoluciones(0)
                .porTipoEquipo(new ArrayList<>())
                .porDia(new ArrayList<>())
                .build();
    }
}
